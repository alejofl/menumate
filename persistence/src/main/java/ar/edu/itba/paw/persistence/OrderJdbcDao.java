package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class OrderJdbcDao implements OrderDao {
    private static final String SELECT_FULL_BASE = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS + ", " + TableFields.ORDER_ITEMS_FIELDS + ", " + TableFields.PRODUCTS_FIELDS + ", " + TableFields.CATEGORIES_FIELDS + " FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id JOIN users on orders.user_id = users.user_id LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id LEFT OUTER JOIN products ON order_items.product_id = products.product_id LEFT OUTER JOIN categories ON products.category_id = categories.category_id";
    private static final String SELECT_FULL_END_ORDER_BY_ID = " ORDER BY orders.order_id, order_items.line_number";
    private static final String SELECT_FULL_END_ORDER_BY_DATE = " ORDER BY orders.date_ordered, orders.order_id, order_items.line_number";

    private static final String SELECT_ITEMLESS_BASE = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS + ", COUNT(*) AS order_item_count, SUM(order_items.quantity*products.price) as order_price FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id JOIN users on orders.user_id = users.user_id LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id LEFT OUTER JOIN products ON order_items.product_id = products.product_id";
    private static final String SELECT_ITEMLESS_END = " GROUP BY orders.order_id, restaurants.restaurant_id, users.user_id";

    private static final String IS_PENDING_COND = "(date_confirmed IS NULL AND date_cancelled IS NULL)";
    private static final String IS_CONFIRMED_COND = "(date_confirmed IS NOT NULL AND date_ready IS NULL AND date_cancelled IS NULL)";
    private static final String IS_READY_COND = "(date_ready IS NOT NULL AND date_delivered IS NULL AND date_cancelled IS NULL)";
    private static final String IS_DELIVERED_COND = "date_delivered IS NOT NULL";
    private static final String IS_CANCELLED_COND = "date_cancelled IS NOT NULL";

    static final String IS_IN_PROGRESS_COND = "(date_delivered IS NULL AND date_cancelled IS NULL)";
    static final String IS_CLOSED_COND = "(date_delivered IS NOT NULL OR date_cancelled IS NOT NULL)";

    private static String getCondStringForOrderStatus(OrderStatus status) {
        switch (status) {
            case PENDING:
                return IS_PENDING_COND;
            case CONFIRMED:
                return IS_CONFIRMED_COND;
            case READY:
                return IS_READY_COND;
            case DELIVERED:
                return IS_DELIVERED_COND;
            default:
                return IS_CANCELLED_COND;
        }
    }

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
            orderData.put("table_number", tableNumber);
        }

        final int orderId = jdbcInsertOrder.executeAndReturnKey(orderData).intValue();

        insertItems(items, orderId);

        return getById(orderId).get();
    }

    private void insertItems(List<OrderItem> items, int orderId) {
        final Map<String, Object>[] orderItemDatas = new Map[items.size()];

        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("order_id", orderId);
            map.put("product_id", item.getProduct().getProductId());
            map.put("line_number", i + 1);
            map.put("quantity", item.getQuantity());
            map.put("comment", item.getComment());
            orderItemDatas[i] = map;
        }

        jdbcInsertOrderItem.executeBatch(orderItemDatas);
    }

    @Override
    public Optional<Order> getById(int orderId) {
        return jdbcTemplate.query(
                SELECT_FULL_BASE + " WHERE orders.order_id = ?" + SELECT_FULL_END_ORDER_BY_ID,
                Extractors.ORDER_EXTRACTOR,
                orderId
        ).stream().findFirst();
    }

    @Override
    public Optional<OrderItemless> getByIdExcludeItems(int orderId) {
        return jdbcTemplate.query(
                SELECT_ITEMLESS_BASE + " WHERE orders.order_id = ?" + SELECT_ITEMLESS_END,
                SimpleRowMappers.ORDER_ITEMLESS_ROW_MAPPER,
                orderId
        ).stream().findFirst();
    }

    @Override
    public PaginatedResult<Order> getByUser(int userId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        List<Order> results = jdbcTemplate.query(
                "WITH orders AS (SELECT * FROM orders WHERE user_id = ? ORDER BY date_ordered LIMIT ? OFFSET ?) " + SELECT_FULL_BASE + SELECT_FULL_END_ORDER_BY_DATE,
                Extractors.ORDER_EXTRACTOR,
                userId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE user_id = ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                userId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<OrderItemless> getByUserExcludeItems(int userId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        RowMapper<OrderItemless> rowMapper = ReusingRowMappers.getOrderItemlessReusingRowMapper();

        List<OrderItemless> results = jdbcTemplate.query(
                SELECT_ITEMLESS_BASE + " WHERE orders.user_id = ? " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered LIMIT ? OFFSET ?",
                rowMapper,
                userId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE user_id = ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                userId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<OrderItemless> getInProgressByUserExcludeItems(int userId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        RowMapper<OrderItemless> rowMapper = ReusingRowMappers.getOrderItemlessReusingRowMapper();

        List<OrderItemless> results = jdbcTemplate.query(
                SELECT_ITEMLESS_BASE + " WHERE orders.user_id = ? AND " + IS_IN_PROGRESS_COND + " " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered LIMIT ? OFFSET ?",
                rowMapper,
                userId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE user_id = ? AND " + IS_IN_PROGRESS_COND,
                SimpleRowMappers.COUNT_ROW_MAPPER,
                userId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        List<Order> results = jdbcTemplate.query(
                "WITH orders AS (SELECT * FROM orders WHERE restaurant_id = ? ORDER BY date_ordered LIMIT ? OFFSET ?) " + SELECT_FULL_BASE + SELECT_FULL_END_ORDER_BY_DATE,
                Extractors.ORDER_EXTRACTOR,
                restaurantId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                restaurantId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(int restaurantId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        RowMapper<OrderItemless> rowMapper = ReusingRowMappers.getOrderItemlessReusingRowMapper();

        List<OrderItemless> results = jdbcTemplate.query(
                SELECT_ITEMLESS_BASE + " WHERE orders.restaurant_id = ? " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered LIMIT ? OFFSET ?",
                rowMapper,
                restaurantId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                restaurantId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        int pageIdx = pageNumber - 1;
        List<Order> results = jdbcTemplate.query(
                "WITH orders AS (SELECT * FROM orders WHERE restaurant_id = ? AND " + getCondStringForOrderStatus(orderStatus) + " ORDER BY date_ordered LIMIT ? OFFSET ?) " + SELECT_FULL_BASE + SELECT_FULL_END_ORDER_BY_DATE,
                Extractors.ORDER_EXTRACTOR,
                restaurantId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ? AND " + getCondStringForOrderStatus(orderStatus),
                SimpleRowMappers.COUNT_ROW_MAPPER,
                restaurantId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(int restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        int pageIdx = pageNumber - 1;
        RowMapper<OrderItemless> rowMapper = ReusingRowMappers.getOrderItemlessReusingRowMapper();

        List<OrderItemless> results = jdbcTemplate.query(
                SELECT_ITEMLESS_BASE + " WHERE orders.restaurant_id = ? AND " + getCondStringForOrderStatus(orderStatus) + " " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered LIMIT ? OFFSET ?",
                rowMapper,
                restaurantId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ? AND " + getCondStringForOrderStatus(orderStatus),
                SimpleRowMappers.COUNT_ROW_MAPPER,
                restaurantId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public boolean markAsConfirmed(int orderId) {
        return jdbcTemplate.update(
                "UPDATE orders SET date_confirmed = now() WHERE order_id = ? AND " + IS_PENDING_COND,
                orderId
        ) > 0;
    }

    @Override
    public boolean markAsReady(int orderId) {
        return jdbcTemplate.update(
                "UPDATE orders SET date_ready = now() WHERE order_id = ? AND " + IS_CONFIRMED_COND,
                orderId
        ) > 0;
    }

    @Override
    public boolean markAsDelivered(int orderId) {
        return jdbcTemplate.update(
                "UPDATE orders SET date_delivered = now() WHERE order_id = ? AND " + IS_READY_COND,
                orderId
        ) > 0;
    }

    @Override
    public boolean markAsCancelled(int orderId) {
        return jdbcTemplate.update(
                "UPDATE orders SET date_cancelled = now() WHERE order_id = ? AND NOT(" + IS_CANCELLED_COND + ") AND NOT(" + IS_DELIVERED_COND + ")",
                orderId
        ) > 0;
    }

    @Override
    public boolean setOrderStatus(int orderId, OrderStatus orderStatus) {
        String sql;
        switch (orderStatus) {
            case PENDING:
                sql = "UPDATE orders SET date_confirmed=NULL, date_ready=NULL, date_delivered=NULL, date_cancelled=NULL WHERE order_id=?";
                break;
            case REJECTED:
                sql = "UPDATE orders SET date_confirmed=NULL, date_ready=NULL, date_delivered=NULL, date_cancelled=COALESCE(date_cancelled, now()) WHERE order_id=?";
                break;
            case CANCELLED:
                sql = "UPDATE orders SET date_delivered=NULL, date_cancelled=COALESCE(date_cancelled, now()) WHERE order_id=?";
                break;
            case CONFIRMED:
                sql = "UPDATE orders SET date_confirmed=COALESCE(date_confirmed, now()), date_ready=NULL, date_delivered=NULL, date_cancelled=NULL WHERE order_id=?";
                break;
            case READY:
                sql = "UPDATE orders SET date_confirmed=COALESCE(date_confirmed, now()), date_ready=COALESCE(date_ready, now()), date_delivered=NULL, date_cancelled=NULL WHERE order_id=?";
                break;
            case DELIVERED:
                sql = "UPDATE orders SET date_confirmed=COALESCE(date_confirmed, now()), date_ready=COALESCE(date_ready, now()), date_delivered=COALESCE(date_delivered, now()), date_cancelled=NULL WHERE order_id=?";
                break;
            default:
                throw new IllegalArgumentException("No such OrderType enum constant");
        }

        return jdbcTemplate.update(sql, orderId) > 0;
    }

    @Override
    public boolean updateAddress(int orderId, String address) {
        return jdbcTemplate.update(
                "UPDATE orders SET address = ? WHERE order_id = ? AND order_type = " + OrderType.DELIVERY.ordinal() + " AND NOT(" + IS_CLOSED_COND + ")",
                address,
                orderId
        ) > 0;
    }

    @Override
    public boolean updateTableNumber(int orderId, int tableNumber) {
        return jdbcTemplate.update(
                "UPDATE orders SET table_number = ? WHERE order_id = ? AND order_type = " + OrderType.DINE_IN.ordinal() + " AND NOT(" + IS_CLOSED_COND + ")",
                tableNumber,
                orderId
        ) > 0;
    }

    @Override
    public boolean delete(int orderId) {
        return jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId) > 0;
    }

    @Override
    public Order createDineIn(int restaurantId, int userId, int tableNumber, List<OrderItem> items) {
        return this.create(OrderType.DINE_IN, restaurantId, userId, null, tableNumber, items);
    }

    @Override
    public Order createTakeaway(int restaurantId, int userId, List<OrderItem> items) {
        return this.create(OrderType.TAKEAWAY, restaurantId, userId, null, null, items);
    }


    @Override
    public Order createDelivery(int restaurantId, int userId, String address, List<OrderItem> items) {
        return this.create(OrderType.DELIVERY, restaurantId, userId, address, null, items);
    }

    @Override
    public OrderItem createOrderItem(Product product, int lineNumber, int quantity, String comment) {
        return new OrderItem(product, lineNumber, quantity, comment);
    }
}
