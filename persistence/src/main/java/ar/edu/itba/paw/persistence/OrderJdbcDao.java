package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrderJdbcDao implements OrderDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderJdbcDao.class);
    private static final String SELECT_FULL_BASE = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS + ", " + TableFields.ORDER_ITEMS_FIELDS + ", " + TableFields.PRODUCTS_FIELDS + ", " + TableFields.CATEGORIES_FIELDS + " FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id JOIN users ON orders.user_id = users.user_id LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id LEFT OUTER JOIN products ON order_items.product_id = products.product_id LEFT OUTER JOIN categories ON products.category_id = categories.category_id";
    private static final String SELECT_FULL_END_ORDER_BY_ID = " ORDER BY orders.order_id, order_items.line_number";
    private static final String SELECT_FULL_END_ORDER_BY_DATE = " ORDER BY orders.date_ordered DESC, orders.order_id, order_items.line_number";

    private static final String SELECT_ITEMLESS_BASE = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS + ", COUNT(*) AS order_item_count, SUM(order_items.quantity*products.price) AS order_price FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id JOIN users ON orders.user_id = users.user_id LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id LEFT OUTER JOIN products ON order_items.product_id = products.product_id";
    private static final String SELECT_ITEMLESS_END = " GROUP BY orders.order_id, restaurants.restaurant_id, users.user_id";

    private static final String IS_PENDING_COND = "(date_confirmed IS NULL AND date_cancelled IS NULL)";
    private static final String IS_CONFIRMED_COND = "(date_confirmed IS NOT NULL AND date_ready IS NULL AND date_cancelled IS NULL)";
    private static final String IS_READY_COND = "(date_ready IS NOT NULL AND date_delivered IS NULL AND date_cancelled IS NULL)";
    private static final String IS_DELIVERED_COND = "date_delivered IS NOT NULL";
    private static final String IS_CANCELLED_COND = "date_cancelled IS NOT NULL";

    static final String IS_IN_PROGRESS_COND = "(date_delivered IS NULL AND date_cancelled IS NULL)";
    static final String IS_CLOSED_COND = "(date_delivered IS NOT NULL OR date_cancelled IS NOT NULL)";

    static final String SELECT_ITEMLESS_ORDERS = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS +
            ", COUNT(*) AS order_item_count, SUM(order_items.quantity*products.price) AS order_price" +
            " FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id" +
            " JOIN users on orders.user_id = users.user_id" +
            " LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id" +
            " LEFT OUTER JOIN products ON order_items.product_id = products.product_id" +
            " GROUP BY orders.order_id, restaurants.restaurant_id, users.user_id";


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

    private Order create(OrderType orderType, long restaurantId, long userId, String address, Integer tableNumber, List<OrderItem> items) {
        if (items.isEmpty())
            throw new IllegalArgumentException("An order must have at least one item");

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
        LOGGER.info("Created order with ID {}", orderId);
        return getById(orderId).get();
    }

    @Transactional
    @Override
    public Order createDineIn(long restaurantId, long userId, int tableNumber, List<OrderItem> items) {
        return this.create(OrderType.DINE_IN, restaurantId, userId, null, tableNumber, items);
    }

    @Transactional
    @Override
    public Order createTakeaway(long restaurantId, long userId, List<OrderItem> items) {
        return this.create(OrderType.TAKEAWAY, restaurantId, userId, null, null, items);
    }

    @Transactional
    @Override
    public Order createDelivery(long restaurantId, long userId, String address, List<OrderItem> items) {
        return this.create(OrderType.DELIVERY, restaurantId, userId, address, null, items);
    }

    @Override
    public OrderItem createOrderItem(Product product, int lineNumber, int quantity, String comment) {
        return new OrderItem(product, lineNumber, quantity, comment);
    }

    private void insertItems(List<OrderItem> items, long orderId) {
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

    private static final String GET_BY_ID_SQL = SELECT_FULL_BASE + " WHERE orders.order_id = ?" + SELECT_FULL_END_ORDER_BY_ID;

    @Override
    public Optional<Order> getById(long orderId) {
        return jdbcTemplate.query(
                GET_BY_ID_SQL,
                Extractors.ORDER_EXTRACTOR,
                orderId
        ).stream().findFirst();
    }

    private static final String GET_BY_ID_EXCLUDEITEMS_SQL = SELECT_ITEMLESS_BASE + " WHERE orders.order_id = ?" + SELECT_ITEMLESS_END;

    @Override
    public Optional<OrderItemless> getByIdExcludeItems(long orderId) {
        return jdbcTemplate.query(
                GET_BY_ID_EXCLUDEITEMS_SQL,
                SimpleRowMappers.ORDER_ITEMLESS_ROW_MAPPER,
                orderId
        ).stream().findFirst();
    }

    private PaginatedResult<Order> getOrderPaginatedResult(long queryTableId, int pageNumber, int pageSize, String getSql, String getCountSql) {
        int pageIdx = pageNumber - 1;
        List<Order> results = jdbcTemplate.query(
                getSql,
                Extractors.ORDER_EXTRACTOR,
                queryTableId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                getCountSql,
                SimpleRowMappers.COUNT_ROW_MAPPER,
                queryTableId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    private PaginatedResult<OrderItemless> getOrderItemlessPaginatedResult(long queryTableId, int pageNumber, int pageSize, String getSql, String getCountSql) {
        int pageIdx = pageNumber - 1;
        RowMapper<OrderItemless> rowMapper = ReusingRowMappers.getOrderItemlessReusingRowMapper();

        List<OrderItemless> results = jdbcTemplate.query(
                getSql,
                rowMapper,
                queryTableId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                getCountSql,
                SimpleRowMappers.COUNT_ROW_MAPPER,
                queryTableId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    private static final String GET_BY_USER_SQL = "WITH orders AS (SELECT * FROM orders WHERE user_id = ? ORDER BY date_ordered DESC LIMIT ? OFFSET ?) " + SELECT_FULL_BASE + SELECT_FULL_END_ORDER_BY_DATE;
    private static final String GET_BY_USER_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE user_id = ?";

    @Override
    public PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize) {
        return getOrderPaginatedResult(userId, pageNumber, pageSize, GET_BY_USER_SQL, GET_BY_USER_COUNT_SQL);
    }

    private final String GET_BY_USER_EXCLUDE_ITEMS_SQL = SELECT_ITEMLESS_BASE + " WHERE orders.user_id = ? " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered DESC LIMIT ? OFFSET ?";
    private final String GET_BY_USER_EXCLUDE_ITEMS_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE user_id = ?";

    @Override
    public PaginatedResult<OrderItemless> getByUserExcludeItems(long userId, int pageNumber, int pageSize) {
        return getOrderItemlessPaginatedResult(userId, pageNumber, pageSize, GET_BY_USER_EXCLUDE_ITEMS_SQL, GET_BY_USER_EXCLUDE_ITEMS_COUNT_SQL);
    }

    private static final String GET_INPROGRESS_BY_USER_EXCLUDE_ITEMS_SQL = SELECT_ITEMLESS_BASE + " WHERE orders.user_id = ? AND " + IS_IN_PROGRESS_COND + " " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered DESC LIMIT ? OFFSET ?";
    private static final String GET_INPROGRESS_BY_USER_EXCLUDE_ITEMS_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE user_id = ? AND " + IS_IN_PROGRESS_COND;

    @Override
    public PaginatedResult<OrderItemless> getInProgressByUserExcludeItems(long userId, int pageNumber, int pageSize) {
        return getOrderItemlessPaginatedResult(userId, pageNumber, pageSize, GET_INPROGRESS_BY_USER_EXCLUDE_ITEMS_SQL, GET_INPROGRESS_BY_USER_EXCLUDE_ITEMS_COUNT_SQL);
    }

    private static final String GET_BY_RESTAURANT_SQL = "WITH orders AS (SELECT * FROM orders WHERE restaurant_id = ? ORDER BY date_ordered DESC LIMIT ? OFFSET ?) " + SELECT_FULL_BASE + SELECT_FULL_END_ORDER_BY_DATE;
    private static final String GET_BY_RESTAURANT_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ?";

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        return getOrderPaginatedResult(restaurantId, pageNumber, pageSize, GET_BY_RESTAURANT_SQL, GET_BY_RESTAURANT_COUNT_SQL);
    }

    private static final String GET_BY_RESTAURANT_EXCLUDEITEMS_SQL = SELECT_ITEMLESS_BASE + " WHERE orders.restaurant_id = ? " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered DESC LIMIT ? OFFSET ?";
    private static final String GET_BY_RESTAURANT_EXCLUDEITEMS_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ?";

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(long restaurantId, int pageNumber, int pageSize) {
        return getOrderItemlessPaginatedResult(restaurantId, pageNumber, pageSize, GET_BY_RESTAURANT_EXCLUDEITEMS_SQL, GET_BY_RESTAURANT_EXCLUDEITEMS_COUNT_SQL);
    }

    private static final String GET_BY_RESTAURANT_AND_STATUS_SQL = "WITH orders AS (SELECT * FROM orders WHERE restaurant_id = ? AND %s ORDER BY date_ordered DESC LIMIT ? OFFSET ?) " + SELECT_FULL_BASE + SELECT_FULL_END_ORDER_BY_DATE;
    private static final String GET_BY_RESTAURANT_AND_STATUS_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ? AND %s";

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        String condString = getCondStringForOrderStatus(orderStatus);

        return getOrderPaginatedResult(
                restaurantId,
                pageNumber,
                pageSize,
                String.format(GET_BY_RESTAURANT_AND_STATUS_SQL, condString),
                String.format(GET_BY_RESTAURANT_AND_STATUS_COUNT_SQL, condString)
        );
    }

    private final static String GET_BY_RESTAURANT_AND_STATUS_EXCLUDEITEMS_SQL = SELECT_ITEMLESS_BASE + " WHERE orders.restaurant_id = ? AND %s " + SELECT_ITEMLESS_END + ", orders.date_ordered ORDER BY orders.date_ordered DESC LIMIT ? OFFSET ?";
    private final static String GET_BY_RESTAURANT_AND_STATUS_EXCLUDEITEMS_COUNT_SQL = "SELECT COUNT(*) AS c FROM orders WHERE restaurant_id = ? AND %s";
    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        String condString = getCondStringForOrderStatus(orderStatus);

        return getOrderItemlessPaginatedResult(
                restaurantId,
                pageNumber,
                pageSize,
                String.format(GET_BY_RESTAURANT_AND_STATUS_EXCLUDEITEMS_SQL, condString),
                String.format(GET_BY_RESTAURANT_AND_STATUS_EXCLUDEITEMS_COUNT_SQL, condString)
        );
    }

    private static final String MARK_AS_CONFIRMED_SQL = "UPDATE orders SET date_confirmed = now() WHERE order_id = ? AND " + IS_PENDING_COND;

    @Override
    public void markAsConfirmed(long orderId) {
        int rows = jdbcTemplate.update(MARK_AS_CONFIRMED_SQL, orderId);
        if (rows == 0) {
            throw new OrderNotFoundException();
        }
        LOGGER.info("Order {} marked as confirmed", orderId);
    }

    private static final String MARK_AS_READY_SQL = "UPDATE orders SET date_ready = now() WHERE order_id = ? AND " + IS_CONFIRMED_COND;

    @Override
    public void markAsReady(long orderId) {
        int rows = jdbcTemplate.update(MARK_AS_READY_SQL, orderId);

        if (rows == 0)
            throw new OrderNotFoundException();
        LOGGER.info("Order {} marked as ready", orderId);
    }

    private static final String MARK_AS_DELIVERED_SQL = "UPDATE orders SET date_delivered = now() WHERE order_id = ? AND " + IS_READY_COND;

    @Override
    public void markAsDelivered(long orderId) {
        int rows = jdbcTemplate.update(MARK_AS_DELIVERED_SQL, orderId);

        if (rows == 0)
            throw new OrderNotFoundException();
        LOGGER.info("Order {} marked as delivered", orderId);
    }

    private static final String MARK_AS_CANCELLED_SQL = "UPDATE orders SET date_cancelled = now() WHERE order_id = ? AND NOT(" + IS_CANCELLED_COND + ") AND NOT(" + IS_DELIVERED_COND + ")";

    @Override
    public void markAsCancelled(long orderId) {
        int rows = jdbcTemplate.update(MARK_AS_CANCELLED_SQL, orderId);

        if (rows == 0)
            throw new OrderNotFoundException();
    }

    @Override
    public void setOrderStatus(long orderId, OrderStatus orderStatus) {
        String sql;
        switch (orderStatus) {
            case PENDING:
                sql = "UPDATE orders SET date_confirmed=NULL, date_ready=NULL, date_delivered=NULL, date_cancelled=NULL WHERE order_id=?";
                LOGGER.info("Order {} set to pending status", orderId);
                break;
            case REJECTED:
                sql = "UPDATE orders SET date_confirmed=NULL, date_ready=NULL, date_delivered=NULL, date_cancelled=COALESCE(date_cancelled, now()) WHERE order_id=?";
                LOGGER.info("Order {} set to rejected status", orderId);
                break;
            case CANCELLED:
                sql = "UPDATE orders SET date_delivered=NULL, date_cancelled=COALESCE(date_cancelled, now()) WHERE order_id=?";
                LOGGER.info("Order {} set to cancelled status", orderId);
                break;
            case CONFIRMED:
                sql = "UPDATE orders SET date_confirmed=COALESCE(date_confirmed, now()), date_ready=NULL, date_delivered=NULL, date_cancelled=NULL WHERE order_id=?";
                LOGGER.info("Order {} set to confirmed status", orderId);
                break;
            case READY:
                sql = "UPDATE orders SET date_confirmed=COALESCE(date_confirmed, now()), date_ready=COALESCE(date_ready, now()), date_delivered=NULL, date_cancelled=NULL WHERE order_id=?";
                LOGGER.info("Order {} set to ready status", orderId);
                break;
            case DELIVERED:
                sql = "UPDATE orders SET date_confirmed=COALESCE(date_confirmed, now()), date_ready=COALESCE(date_ready, now()), date_delivered=COALESCE(date_delivered, now()), date_cancelled=NULL WHERE order_id=?";
                LOGGER.info("Order {} set to delivered status", orderId);
                break;
            default:
                throw new IllegalArgumentException("No such OrderType enum constant");
        }

        int rows = jdbcTemplate.update(sql, orderId);

        if (rows == 0)
            throw new OrderNotFoundException();
    }

    private static final String UPDATE_ADDRESS_SQL = "UPDATE orders SET address = ? WHERE order_id = ? AND order_type = " + OrderType.DELIVERY.ordinal() + " AND " + IS_IN_PROGRESS_COND;

    @Override
    public void updateAddress(long orderId, String address) {
        int rows = jdbcTemplate.update(UPDATE_ADDRESS_SQL, address, orderId);

        if (rows == 0)
            throw new OrderNotFoundException();
        LOGGER.info("Order {} address updated to {}", orderId, address);
    }

    private static final String UPDATE_TABLENUMBER_SQL = "UPDATE orders SET table_number = ? WHERE order_id = ? AND order_type = " + OrderType.DINE_IN.ordinal() + " AND " + IS_IN_PROGRESS_COND;

    @Override
    public void updateTableNumber(long orderId, int tableNumber) {
        int rows = jdbcTemplate.update(UPDATE_TABLENUMBER_SQL, tableNumber, orderId);

        if (rows == 0)
            throw new OrderNotFoundException();
        LOGGER.info("Order {} table number updated to {}", orderId, tableNumber);
    }
}
