package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.*;
import ar.edu.itba.paw.util.PaginatedResult;
import org.junit.Assert;
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
        Order order = orderDao.createDineIn(RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ACTIVE_USER_ID, OrderConstants.DEFAULT_ORDER_TABLE);
        em.flush();

        Assert.assertEquals(OrderType.DINE_IN, order.getOrderType());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), order.getRestaurantId());
        Assert.assertEquals(UserConstants.ACTIVE_USER_ID, order.getUserId());
    }

    @Test
    @Rollback
    public void testCreationTakeaway() {
        Order order = orderDao.createTakeaway(RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ACTIVE_USER_ID);
        em.flush();

        Assert.assertEquals(OrderType.TAKEAWAY, order.getOrderType());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), order.getRestaurantId());
        Assert.assertEquals(UserConstants.ACTIVE_USER_ID, order.getUserId());
    }

    @Test
    @Rollback
    public void testCreationDelivery() {
        Order order = orderDao.createDelivery(RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ACTIVE_USER_ID, OrderConstants.DEFAULT_ORDER_ADDRESS);
        em.flush();

        Assert.assertEquals(OrderType.DELIVERY, order.getOrderType());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), order.getRestaurantId());
        Assert.assertEquals(UserConstants.ACTIVE_USER_ID, order.getUserId());
        Assert.assertEquals(OrderConstants.DEFAULT_ORDER_ADDRESS, order.getAddress());
    }

    @Test
    public void testFindActiveOrdersById() {
        Optional<Order> order = orderDao.getById(OrderConstants.ORDER_IDS_RESTAURANT_0[0]);

        Assert.assertTrue(order.isPresent());
        Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[0].longValue(), order.get().getOrderId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), order.get().getRestaurantId());
        Assert.assertEquals(UserConstants.ACTIVE_USER_ID, order.get().getUserId());
        Assert.assertEquals(OrderConstants.DEFAULT_ORDER_TYPE, order.get().getOrderType());
        Assert.assertNotNull(order.get().getDateOrdered());
    }

    @Test
    public void testGetOrderByIdWithOrderItems() {
        Optional<Order> order = orderDao.getById(ProductConstants.ORDER_ITEMS_FOR_ORDER_IDS[0]);

        Assert.assertTrue(order.isPresent());
        List<OrderItem> orderItems = order.get().getItems();
        Assert.assertNotNull(orderItems);
        Assert.assertEquals(1, orderItems.size());
        for (OrderItem oi: orderItems) {
            Assert.assertEquals(ProductConstants.PRODUCTS_FOR_ORDER_IDS[0], oi.getProduct().getProductId());
            Assert.assertEquals(ProductConstants.DEFAULT_ORDER_ITEM_QUANTITY, oi.getQuantity());
            Assert.assertEquals(ProductConstants.DEFAULT_ORDER_ITEM_COMMENT, oi.getComment());
            Assert.assertEquals(ProductConstants.LINE_NUMBER_FOR_ORDER_IDS[0].longValue(), oi.getLineNumber());
        }
    }

    @Test
    public void testFindNoOrderById() {
        Optional<Order> order = orderDao.getById(NON_EXISTING_ORDER_ID);

        Assert.assertFalse(order.isPresent());
    }

    @Test
    public void testFindActiveOrdersByUserId() {
        List<Order> orders = orderDao.getByUser(UserConstants.ACTIVE_USER_ID, 1, OrderConstants.TOTAL_ORDER_COUNT * 2, false, true).getResult();

        Assert.assertNotNull(orders);
        Assert.assertEquals(OrderConstants.TOTAL_ORDER_COUNT, orders.size());

        for (Order o: orders) {
            Assert.assertEquals(UserConstants.ACTIVE_USER_ID, o.getUserId());
        }
    }

    @Test
    public void testFindEmptyOrdersByUserId() {
        List<Order> orders = orderDao.getByUser(UserConstants.RESTAURANT_OWNER_ID, 1, OrderConstants.TOTAL_ORDER_COUNT * 2, false, true).getResult();

        Assert.assertNotNull(orders);
        Assert.assertEquals(0, orders.size());
    }

    @Test
    public void testFindActiveOrdersByRestaurantIdPagedWithNoOrderStatus() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_0.length;
        PaginatedResult<Order> page = orderDao.getByRestaurant(RestaurantConstants.RESTAURANT_IDS[0], 1, totalOrders, null, true);

        Assert.assertEquals(totalOrders, page.getTotalCount());
        Assert.assertEquals(totalOrders, page.getResult().size());

        List<Order> allOrders = page.getResult();
        for (int i = 0; i < totalOrders; i++) {
            Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[i].longValue(), allOrders.get(i).getOrderId());
            Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], allOrders.get(i).getRestaurant().getRestaurantId());
            Assert.assertEquals(UserConstants.ACTIVE_USER_ID, allOrders.get(i).getUserId());
            Assert.assertEquals(OrderConstants.DEFAULT_ORDER_TYPE, allOrders.get(i).getOrderType());
            Assert.assertNotNull(allOrders.get(i).getDateOrdered());
        }
    }

    @Test
    @Rollback
    public void testFindOrdersByRestaurantIdPagedWithOrderStatusConfirmed() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_1.length;
        for(Long ids : OrderConstants.ORDER_IDS_RESTAURANT_1) {
            Order order = em.find(Order.class, ids);
            order.setDateConfirmed(LocalDateTime.now());
        }

        PaginatedResult<Order> page = orderDao.getByRestaurant(RestaurantConstants.RESTAURANT_IDS[1], 1, totalOrders, OrderStatus.CONFIRMED, true);

        Assert.assertEquals(totalOrders, page.getTotalCount());
        Assert.assertEquals(totalOrders, page.getResult().size());

        List<Order> allOrders = page.getResult();
        for (int i = 0; i < totalOrders; i++) {
            Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_1[i].longValue(), allOrders.get(i).getOrderId());
            Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[1], allOrders.get(i).getRestaurant().getRestaurantId());
            Assert.assertEquals(UserConstants.ACTIVE_USER_ID, allOrders.get(i).getUserId());
            Assert.assertEquals(OrderConstants.DEFAULT_ORDER_TYPE, allOrders.get(i).getOrderType());
            Assert.assertNotNull(allOrders.get(i).getDateOrdered());
        }
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "orders", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[1] + " AND date_confirmed IS NULL"));
    }
}