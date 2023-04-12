package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class OrderJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private OrderJdbcDao orderJdbcDao;
    private JdbcTemplate jdbcTemplate;
    private static final long ORDER_ID = 1;
    private static final String ADDRESS = "Calle 123";
    private static final int TABLE_NUMBER = 10;
    private static final String[] ADDRESSES = {"Calle 123", "Calle 456", "Calle 789", "Calle 101112", "Calle 131415", "Calle 123"};
    private static final Integer[] TABLE_NUMBERS = {1, 2, 3, 4, 5, 6, 10, 10, 10, 11};
    private static final long RESTAURANT_ID = 1;
    private static final String RESTAURANT_NAME = "RESTAURANT_NAME";
    private static final long USER_ID = 1;
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String EMAIL = "EMAIL@EMAIL.COM";
    private static final long ORDER_TYPE_ID = 1;
    private static final String ORDER_TYPE_NAME = "ORDER_TYPE_NAME";


    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"order_types", "restaurants", "users", "orders");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name) VALUES (" + RESTAURANT_ID +", '" + RESTAURANT_NAME + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, username, password, email) VALUES (" + USER_ID +", '" + USERNAME + "', '" + PASSWORD + "', '" + EMAIL + "')");
        jdbcTemplate.execute("INSERT INTO order_types (order_type_id, name) VALUES (" + ORDER_TYPE_ID +", '" + ORDER_TYPE_NAME + "')");
    }

    @Test
    public void testCreation() throws SQLException {
        Order order = orderJdbcDao.create(RESTAURANT_ID, USER_ID, ORDER_TYPE_ID);
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId());
        Assert.assertEquals(ORDER_TYPE_ID, order.getOrderTypeId());
        Assert.assertEquals(ORDER_ID, order.getOrderId());
    }

    @Test
    public void testFindOrdersById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id) VALUES (" + ORDER_ID +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ")");
        Optional<Order> order = orderJdbcDao.findOrderById(ORDER_ID);
        Assert.assertTrue(order.isPresent());
        Assert.assertEquals(ORDER_ID, order.get().getOrderId());
        Assert.assertEquals(RESTAURANT_ID, order.get().getRestaurantId());
        Assert.assertEquals(USER_ID, order.get().getUserId());
        Assert.assertEquals(ORDER_TYPE_ID, order.get().getOrderTypeId());
        Assert.assertNotNull(order.get().getOrderDate());
    }

    @Test
    public void testFindOrdersByUserId() throws SQLException {
        int iters = new Random().nextInt(7) + 3;
        for(int i = 0; i < iters; i++)
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id) VALUES (" + i +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ")");

        List<Order> orders = orderJdbcDao.findOrdersByUserId(USER_ID, RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for(int i = 0; i < iters; i++) {
            Assert.assertEquals(i, orders.get(i).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i).getUserId());
            Assert.assertEquals(ORDER_TYPE_ID, orders.get(i).getOrderTypeId());
            Assert.assertNotNull(orders.get(i).getOrderDate());
        }
    }

    @Test
    public void testFindOrdersByRestaurantId() throws SQLException {
        int iters = new Random().nextInt(7) + 3;
        for(int i = 1; i < iters + 1; i++)
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id) VALUES (" + i +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ")");

        List<Order> orders = orderJdbcDao.findOrdersByRestaurantId(RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for(int i = 1; i < iters + 1; i++) {
            Assert.assertEquals(i, orders.get(i-1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i-1).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i-1).getUserId());
            Assert.assertEquals(ORDER_TYPE_ID, orders.get(i-1).getOrderTypeId());
            Assert.assertNotNull(orders.get(i-1).getOrderDate());
        }
    }

    @Test
    public void testFindOrdersByOrderTypeId() throws SQLException {
        int iters = new Random().nextInt(7) + 3;
        for(int i = 1; i < iters + 1; i++)
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id) VALUES (" + i +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ")");

        List<Order> orders = orderJdbcDao.findByOrdersTypeId(ORDER_TYPE_ID, RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for(int i = 1; i < iters; i++) {
            Assert.assertEquals(i, orders.get(i-1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i-1).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i-1).getUserId());
            Assert.assertEquals(ORDER_TYPE_ID, orders.get(i-1).getOrderTypeId());
            Assert.assertNotNull(orders.get(i-1).getOrderDate());
        }
    }

    @Test
    public void testFindOrdersByOrderDate() throws SQLException {
        int iters = new Random().nextInt(7) + 3;
        for(int i = 1; i < iters + 1; i++)
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id) VALUES (" + i +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ")");

        List<Order> orders = orderJdbcDao.findByOrdersBetweenDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1), RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for(int i = 1; i < iters + 1; i++) {
            Assert.assertEquals(i, orders.get(i-1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i-1).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i-1).getUserId());
            Assert.assertEquals(ORDER_TYPE_ID, orders.get(i-1).getOrderTypeId());
            Assert.assertNotNull(orders.get(i-1).getOrderDate());
        }
    }

    @Test
    public void testFindOrdersByAddress() throws SQLException {

        long count = Arrays.stream(ADDRESSES)
                .filter(str -> str.equals(ADDRESS))
                .count();

        for(int i=1; i<ADDRESSES.length + 1; i++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id, address) VALUES (" + i +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ", '" + ADDRESSES[i-1] + "')");
        }

        List<Order> orders = orderJdbcDao.findByOrdersAddress(ADDRESS, RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(count, orders.size());
        for(int i=0; i<count; i++){
            Assert.assertEquals(RESTAURANT_ID, orders.get(i).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i).getUserId());
            Assert.assertEquals(ORDER_TYPE_ID, orders.get(i).getOrderTypeId());
            Assert.assertNotNull(orders.get(i).getOrderDate());
            Assert.assertEquals(ADDRESS, orders.get(i).getAddress());
        }
    }

    @Test
    public void testFindOrdersByTableNumber() throws SQLException {
        long count = Arrays.stream(TABLE_NUMBERS)
                .filter(num -> num.equals(TABLE_NUMBER))
                .count();

        for(int i=1; i<TABLE_NUMBERS.length + 1; i++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id, table_number) VALUES (" + i +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ", '" + TABLE_NUMBERS[i-1] + "')");
        }

        List<Order> orders = orderJdbcDao.findByOrdersTableNumber(TABLE_NUMBER, RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(count, orders.size());
        for(int i=0; i<count; i++){
            Assert.assertEquals(RESTAURANT_ID, orders.get(i).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i).getUserId());
            Assert.assertEquals(ORDER_TYPE_ID, orders.get(i).getOrderTypeId());
            Assert.assertNotNull(orders.get(i).getOrderDate());
            Assert.assertEquals(TABLE_NUMBER, orders.get(i).getTableNumber());
        }
    }

    @Test
    public void testUpdateAddress() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id, address) VALUES (" + ORDER_ID +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ", '" + ADDRESS + "')");
        Assert.assertTrue(orderJdbcDao.updateAddress(ORDER_ID, "newAddress"));
    }

    @Test
    public void testUpdateTableNumber() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type_id, table_number) VALUES (" + ORDER_ID +", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE_ID + ", '" + TABLE_NUMBER + "')");
        Assert.assertTrue(orderJdbcDao.updateTableNumber(ORDER_ID, TABLE_NUMBER + 10));
    }

}
