package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.OrderConstants;
import ar.edu.itba.paw.persistence.constants.ProductConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
import ar.edu.itba.paw.util.PaginatedResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class OrderJpaDaoTest {

    public static final long NON_EXISTING_ORDER_ID = 99999;

    @Autowired
    private DataSource ds;

    @Autowired
    private OrderJpaDao orderDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreationDineIn() {
        final Order order = orderDao.createDineIn(RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ACTIVE_USER_ID, OrderConstants.DEFAULT_ORDER_TABLE);
        em.flush();

        assertEquals(OrderType.DINE_IN, order.getOrderType());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], order.getRestaurantId());
        assertEquals(UserConstants.ACTIVE_USER_ID, order.getUserId());
    }

    @Test
    @Rollback
    public void testCreationTakeaway() {
        final Order order = orderDao.createTakeaway(RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ACTIVE_USER_ID);
        em.flush();

        assertEquals(OrderType.TAKEAWAY, order.getOrderType());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], order.getRestaurantId());
        assertEquals(UserConstants.ACTIVE_USER_ID, order.getUserId());
    }

    @Test
    @Rollback
    public void testCreationDelivery() {
        final Order order = orderDao.createDelivery(RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ACTIVE_USER_ID, OrderConstants.DEFAULT_ORDER_ADDRESS);
        em.flush();

        assertEquals(OrderType.DELIVERY, order.getOrderType());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], order.getRestaurantId());
        assertEquals(UserConstants.ACTIVE_USER_ID, order.getUserId());
        assertEquals(OrderConstants.DEFAULT_ORDER_ADDRESS, order.getAddress());
    }

    @Test
    public void testFindActiveOrdersById() {
        final Optional<Order> order = orderDao.getById(OrderConstants.ORDER_IDS_RESTAURANT_0[0]);

        assertTrue(order.isPresent());
        assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[0], order.get().getOrderId().longValue());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], order.get().getRestaurantId());
        assertEquals(UserConstants.ACTIVE_USER_ID, order.get().getUserId());
        assertEquals(OrderConstants.DEFAULT_ORDER_TYPE, order.get().getOrderType());
        assertNotNull(order.get().getDateOrdered());
    }

    @Test
    public void testGetOrderByIdWithOrderItems() {
        final Optional<Order> order = orderDao.getById(ProductConstants.ORDER_ITEMS_FOR_ORDER_IDS[0]);

        assertTrue(order.isPresent());
        final List<OrderItem> orderItems = order.get().getItems();
        assertNotNull(orderItems);
        assertEquals(1, orderItems.size());
        for (OrderItem oi : orderItems) {
            assertEquals(ProductConstants.PRODUCTS_FOR_ORDER_IDS[0], oi.getProduct().getProductId().longValue());
            assertEquals(ProductConstants.DEFAULT_ORDER_ITEM_QUANTITY, oi.getQuantity());
            assertEquals(ProductConstants.DEFAULT_ORDER_ITEM_COMMENT, oi.getComment());
            assertEquals(ProductConstants.LINE_NUMBER_FOR_ORDER_IDS[0], oi.getLineNumber());
        }
    }

    @Test
    public void testFindNoOrderById() {
        final Optional<Order> order = orderDao.getById(NON_EXISTING_ORDER_ID);

        assertFalse(order.isPresent());
    }

    @Test
    public void testFindActiveOrdersByUserId() {
        final List<Order> orders = orderDao.get(UserConstants.ACTIVE_USER_ID, null, null, false, true, 1, OrderConstants.TOTAL_ORDER_COUNT * 2).getResult();

        assertNotNull(orders);
        assertEquals(OrderConstants.TOTAL_ORDER_COUNT, orders.size());

        for (Order o : orders) {
            assertEquals(UserConstants.ACTIVE_USER_ID, o.getUserId());
        }
    }

    @Test
    public void testFindEmptyOrdersByUserId() {
        final List<Order> orders = orderDao.get(UserConstants.RESTAURANT_OWNER_ID, null, null, false, true, 1, OrderConstants.TOTAL_ORDER_COUNT * 2).getResult();

        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    public void testFindActiveOrdersByRestaurantIdPagedWithNoOrderStatus() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_0.length;
        final PaginatedResult<Order> page = orderDao.get(null, RestaurantConstants.RESTAURANT_IDS[0], null, false, true, 1, totalOrders);


        assertEquals(totalOrders, page.getTotalCount());
        assertEquals(totalOrders, page.getResult().size());

        final List<Order> allOrders = page.getResult();
        for (int i = 0; i < totalOrders; i++) {
            assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[i], allOrders.get(i).getOrderId().longValue());
            assertEquals(RestaurantConstants.RESTAURANT_IDS[0], allOrders.get(i).getRestaurantId());
            assertEquals(UserConstants.ACTIVE_USER_ID, allOrders.get(i).getUserId());
            assertEquals(OrderConstants.DEFAULT_ORDER_TYPE, allOrders.get(i).getOrderType());
            assertNotNull(allOrders.get(i).getDateOrdered());
        }
    }

    @Test
    @Rollback
    public void testFindOrdersByRestaurantIdPagedWithOrderStatusConfirmed() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_1.length;
        for (Long ids : OrderConstants.ORDER_IDS_RESTAURANT_1) {
            Order order = em.find(Order.class, ids);
            order.setDateConfirmed(LocalDateTime.now());
        }

        final PaginatedResult<Order> page = orderDao.get(null, RestaurantConstants.RESTAURANT_IDS[1], OrderStatus.CONFIRMED, false, true, 1, totalOrders);

        assertEquals(totalOrders, page.getTotalCount());
        assertEquals(totalOrders, page.getResult().size());

        final List<Order> allOrders = page.getResult();
        for (int i = 0; i < totalOrders; i++) {
            assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_1[i], allOrders.get(i).getOrderId().longValue());
            assertEquals(RestaurantConstants.RESTAURANT_IDS[1], allOrders.get(i).getRestaurantId());
            assertEquals(UserConstants.ACTIVE_USER_ID, allOrders.get(i).getUserId());
            assertEquals(OrderConstants.DEFAULT_ORDER_TYPE, allOrders.get(i).getOrderType());
            assertNotNull(allOrders.get(i).getDateOrdered());
        }
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "orders", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[1] + " AND date_confirmed IS NULL"));
    }

    @Test
    @Rollback
    public void testCancelPendingOrders() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_1.length;
        orderDao.cancelNonDeliveredOrders(RestaurantConstants.RESTAURANT_IDS[1]);
        assertEquals(totalOrders, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "orders", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[1] + "AND date_cancelled IS NOT NULL AND date_ordered IS NOT NULL AND date_confirmed IS NULL AND date_ready IS NULL AND date_delivered IS NULL"));
    }

    @Test
    @Rollback
    public void testCancelConfirmedOrders() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_1.length;
        for (Long ids : OrderConstants.ORDER_IDS_RESTAURANT_1) {
            Order order = em.find(Order.class, ids);
            order.setDateConfirmed(LocalDateTime.now());
        }
        orderDao.cancelNonDeliveredOrders(RestaurantConstants.RESTAURANT_IDS[1]);
        assertEquals(totalOrders, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "orders", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[1] + "AND date_cancelled IS NOT NULL AND date_ordered IS NOT NULL AND date_confirmed IS NOT NULL AND date_ready IS NULL AND date_delivered IS NULL"));
    }

    @Test
    @Rollback
    public void testCancelReadyOrders() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_1.length;
        for (Long ids : OrderConstants.ORDER_IDS_RESTAURANT_1) {
            Order order = em.find(Order.class, ids);
            order.setDateConfirmed(LocalDateTime.now());
            order.setDateReady(LocalDateTime.now());
        }
        orderDao.cancelNonDeliveredOrders(RestaurantConstants.RESTAURANT_IDS[1]);
        assertEquals(totalOrders, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "orders", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[1] + "AND date_cancelled IS NOT NULL AND date_ordered IS NOT NULL AND date_confirmed IS NOT NULL AND date_ready IS NOT NULL AND date_delivered IS NULL"));
    }

    @Test
    @Rollback
    public void testAttemptCancelDeliveredOrders() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_1.length;
        for (Long ids : OrderConstants.ORDER_IDS_RESTAURANT_1) {
            Order order = em.find(Order.class, ids);
            order.setDateConfirmed(LocalDateTime.now());
            order.setDateReady(LocalDateTime.now());
            order.setDateDelivered(LocalDateTime.now());
        }
        orderDao.cancelNonDeliveredOrders(RestaurantConstants.RESTAURANT_IDS[1]);
        assertEquals(totalOrders, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "orders", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[1] + "AND date_cancelled IS NULL AND date_ordered IS NOT NULL AND date_confirmed IS NOT NULL AND date_ready IS NOT NULL AND date_delivered IS NOT NULL"));
    }
}