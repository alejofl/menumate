package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderType;
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
    private static final String SelectEnd = " ORDER BY orders.order_id, order_items.line_number";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public OrderJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("orders")
                .usingColumns("order_type", "restaurant_id", "user_id", "table_number", "address")
                .usingGeneratedKeyColumns("order_id");
    }


    @Override
    public Order create(OrderType orderType, long restaurantId, long userId) {
        final Map<String, Object> orderData = new HashMap<>();
        orderData.put("order_type", orderType.ordinal());
        orderData.put("restaurant_id", restaurantId);
        orderData.put("user_id", userId);
        final long orderId = jdbcInsert.executeAndReturnKey(orderData).longValue();

        return getById(orderId).get();
    }

    @Override
    public Optional<Order> getById(long orderId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.order_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                orderId
        ).stream().findFirst();
    }

    @Override
    public List<Order> getByUser(long userId, long restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.user_id = ? AND orders.restaurant_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                userId,
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurant(long restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.restaurant_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                restaurantId
        );
    }

    @Override
    public List<Order> getByOrderTypeAndRestaurant(OrderType orderType, long restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.order_type = ? AND orders.restaurant_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                orderType.ordinal(),
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurantOrderedBetweenDates(long restaurantId, LocalDateTime start, LocalDateTime end) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.date_ordered BETWEEN ? AND ? AND orders.restaurant_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end),
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurantAndAddress(long restaurantId, String address) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.address = ? AND orders.restaurant_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                address,
                restaurantId
        );
    }

    @Override
    public List<Order> getByRestaurantAndTableNumber(long restaurantId, int tableNumber) {
        return jdbcTemplate.query(
                SelectBase + " WHERE orders.table_number = ? AND orders.restaurant_id = ?" + SelectEnd,
                Extractors.ORDER_EXTRACTOR,
                tableNumber,
                restaurantId
        );
    }

    @Override
    public boolean updateAddress(long orderId, String address) {
        return jdbcTemplate.update("UPDATE orders SET address = ? WHERE order_id = ?", address, orderId) > 0;
    }

    @Override
    public boolean updateTableNumber(long orderId, int tableNumber) {
        return jdbcTemplate.update("UPDATE orders SET table_number = ? WHERE order_id = ?", tableNumber, orderId) > 0;
    }

    @Override
    public boolean delete(long orderId) {
        return jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId) > 0;
    }
}
