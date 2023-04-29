package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrderJdbcDao implements OrderDao {

    private static final String SelectBase = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS + ", " + TableFields.ORDER_ITEMS_FIELDS + ", " + TableFields.PRODUCTS_FIELDS + ", " + TableFields.CATEGORIES_FIELDS + " FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id JOIN users on orders.user_id = users.user_id LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id LEFT OUTER JOIN products ON order_items.product_id = products.product_id LEFT OUTER JOIN categories ON products.category_id = categories.category_id";
    private static final String SelectEndOrderById = " ORDER BY orders.order_id, order_items.line_number";
    private static final String SelectEndOrderByDate = " ORDER BY orders.date_ordered, order_items.line_number";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertOrder;
    private final SimpleJdbcInsert jdbcInsertOrderItem;


    @Autowired
    public OrderJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsertOrder = new SimpleJdbcInsert(ds)
                .withTableName("orders")
                .usingColumns("order_type", "restaurant_id", "user_id", "table_number", "address")
                .usingGeneratedKeyColumns("order_id");
        jdbcInsertOrderItem = new SimpleJdbcInsert(ds)
                .withTableName("order_items")
                .usingColumns("order_id", "product_id", "line_number", "quantity", "comment");
    }

    private Order create(OrderType orderType, int restaurantId, int userId, String address, Integer tableNumber, List<OrderItem> items) {
        final Map<String, Object> orderData = new HashMap<>();
        orderData.put("order_type", orderType.ordinal());
        orderData.put("restaurant_id", restaurantId);
        orderData.put("user_id", userId);

        if (address != null) {
            orderData.put("address", address);
        }

        if (tableNumber != null) {
            orderData.put("tableNumber", tableNumber);
        }

        final int orderId = jdbcInsertOrder.executeAndReturnKey(orderData).intValue();

        insertItems(items, orderId);

        return getById(orderId).get();
    }

    private void insertItems(List<OrderItem> items, int orderId) {
        for (OrderItem item : items) {
            final Map<String, Object> orderItemData = new HashMap<>();
            orderItemData.put("order_id", orderId);
            orderItemData.put("product_id", item.getProduct().getProductId());
            orderItemData.put("line_number", item.getLineNumber());
            orderItemData.put("quantity", item.getQuantity());
            orderItemData.put("comment", item.getComment());
            jdbcInsertOrderItem.execute(orderItemData);
        }
    }

    @Override
    public Optional<Order> getById(int orderId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.order_id = ?" + SelectEndOrderById,
                Extractors.ORDER_EXTRACTOR,
                orderId
        ).stream().findFirst();
    }

    @Override
    public List<Order> getByUser(int userId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.user_id = ?" + SelectEndOrderByDate,
                Extractors.ORDER_EXTRACTOR,
                userId
        );
    }

    @Override
    public List<Order> getByRestaurant(int restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.restaurant_id = ?" + SelectEndOrderByDate,
                Extractors.ORDER_EXTRACTOR,
                restaurantId
        );
    }

    @Override
    public List<Order> getByOrderTypeAndRestaurant(OrderType orderType, int restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.order_type = ? AND orders.restaurant_id = ?" + SelectEndOrderByDate,
                Extractors.ORDER_EXTRACTOR,
                orderType.ordinal(),
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurantOrderedBetweenDates(int restaurantId, LocalDateTime start, LocalDateTime end) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.date_ordered BETWEEN ? AND ? AND orders.restaurant_id = ?" + SelectEndOrderByDate,
                Extractors.ORDER_EXTRACTOR,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end),
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurantAndAddress(int restaurantId, String address) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.address = ? AND orders.restaurant_id = ?" + SelectEndOrderByDate,
                Extractors.ORDER_EXTRACTOR,
                address,
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurantAndTableNumber(int restaurantId, int tableNumber) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.table_number = ? AND orders.restaurant_id = ?" + SelectEndOrderByDate,
                Extractors.ORDER_EXTRACTOR,
                tableNumber,
                restaurantId
        );
    }

    @Override
    public boolean updateAddress(int orderId, String address) {
        return jdbcTemplate.update("UPDATE orders SET address = ? WHERE order_id = ?", address, orderId) > 0;
    }

    @Override
    public boolean updateTableNumber(int orderId, int tableNumber) {
        return jdbcTemplate.update("UPDATE orders SET table_number = ? WHERE order_id = ?", tableNumber, orderId) > 0;
    }

    @Override
    public boolean delete(int orderId) {
        return jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId) > 0;
    }

    // DineIn
    @Override
    public Order create(OrderType orderType, int restaurantId, int userId, int tableNumber, List<OrderItem> items) {
        return this.create(orderType, restaurantId, userId, null, tableNumber, items);
    }

    // Takeaway
    @Override
    public Order create(OrderType orderType, int restaurantId, int userId, List<OrderItem> items) {
        return this.create(orderType, restaurantId, userId, null, null, items);
    }


    // Delivery
    @Override
    public Order create(OrderType orderType, int restaurantId, int userId, String address, List<OrderItem> items) {
        return this.create(orderType, restaurantId, userId, address, null, items);
    }

    @Override
    public OrderItem createOrderItem(Product product, int lineNumber, int quantity, String comment) {
        return new OrderItem(product, lineNumber, quantity, comment);
    }
}
