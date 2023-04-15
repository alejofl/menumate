package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.Product;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class OrderJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private OrderJdbcDao orderJdbcDao;
    private JdbcTemplate jdbcTemplate;
    private static final long ORDER_ID = 8844;
    private static final String ADDRESS = "Calle 123";
    private static final int TABLE_NUMBER = 11;
    private static final String[] ADDRESSES = {"Calle 123", "Calle 456", "Calle 789", "Calle 101112", "Calle 131415", "Calle 123"};
    private static final Integer[] TABLE_NUMBERS = {1, 2, 3, 4, 5, 6, 10, 10, 10, 11};
    private static final long RESTAURANT_ID = 45123;
    private static final String RESTAURANT_NAME = "EmpanadasMorita";
    private static final String RESTAURANT_EMAIL = "ivawashere@email.com";
    private static final long USER_ID = 71823;
    private static final String USERNAME = "UsuarioCopado33";
    private static final String PASSWORD = "ElSecretoDeVictoria";
    private static final String NAME = "pepito";
    private static final String EMAIL = "usuario@copado.com";
    private static final OrderType ORDER_TYPE = OrderType.DINE_IN;
    private static final long PRODUCT_ID = 212;
    private static final String PRODUCT_NAME = "Pepinito con sal";
    private static final double PRODUCT_PRICE = 533.55;
    private static final long CATEGORY_ID = 12421;
    private static final String CATEGORY_NAME = "Postgres Dulces";
    private static final int CATEGORY_ORDER = 10;
    private List<OrderItem> orderItemList;



    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "restaurants", "users", "orders");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, username, password, email, name) VALUES (" + USER_ID + ", '" + USERNAME + "', '" + PASSWORD + "', '" + EMAIL + "', '" + NAME + "')");
        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "', " + CATEGORY_ORDER + ")");
        jdbcTemplate.execute("INSERT INTO products(product_id, category_id, name, price) VALUES (" + PRODUCT_ID + ", " + CATEGORY_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ")");
        orderItemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Product product = mock(Product.class);
            OrderItem orderItem = new OrderItem(product, i+1, i+2, "comentario " + (i+1));
            orderItemList.add(orderItem);
        }
    }

    @Test
    public void testCreationDineIn() throws SQLException {
        Order order = orderJdbcDao.create(ORDER_TYPE, RESTAURANT_ID, USER_ID, TABLE_NUMBER, orderItemList);
        Assert.assertEquals(ORDER_TYPE, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurant().getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUser().getUserId());

        // Testing ordered items
        List<OrderItem> dataToTest = order.getItems();
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        for (int i=0 ; i<orderItemList.size() ; i++){
            Assert.assertEquals(orderItemList.get(i).getProduct().getProductId(), dataToTest.get(i).getProduct().getProductId());
            Assert.assertEquals(orderItemList.get(i).getComment(), dataToTest.get(i).getComment());
            Assert.assertEquals(orderItemList.get(i).getQuantity(), dataToTest.get(i).getQuantity());
            Assert.assertEquals(orderItemList.get(i).getLineNumber(), dataToTest.get(i).getLineNumber());
        }
    }

    @Test
    public void testCreationTakeaway() throws SQLException {
        Order order = orderJdbcDao.create(ORDER_TYPE, RESTAURANT_ID, USER_ID, orderItemList);
        Assert.assertEquals(ORDER_TYPE, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurant().getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUser().getUserId());

        // Testing ordered items
        List<OrderItem> dataToTest = order.getItems();
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        for (int i=0 ; i<orderItemList.size() ; i++){
            Assert.assertEquals(orderItemList.get(i).getProduct().getProductId(), dataToTest.get(i).getProduct().getProductId());
            Assert.assertEquals(orderItemList.get(i).getComment(), dataToTest.get(i).getComment());
            Assert.assertEquals(orderItemList.get(i).getQuantity(), dataToTest.get(i).getQuantity());
            Assert.assertEquals(orderItemList.get(i).getLineNumber(), dataToTest.get(i).getLineNumber());
        }
    }

    @Test
    public void testCreationDelivery() throws SQLException {
        Order order = orderJdbcDao.create(ORDER_TYPE, RESTAURANT_ID, USER_ID, ADDRESS, orderItemList);
        Assert.assertEquals(ORDER_TYPE, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurant().getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUser().getUserId());
        Assert.assertEquals(ADDRESS, order.getAddress());

        // Testing ordered items
        List<OrderItem> dataToTest = order.getItems();
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        for (int i=0 ; i<orderItemList.size() ; i++){
            Assert.assertEquals(orderItemList.get(i).getProduct().getProductId(), dataToTest.get(i).getProduct().getProductId());
            Assert.assertEquals(orderItemList.get(i).getComment(), dataToTest.get(i).getComment());
            Assert.assertEquals(orderItemList.get(i).getQuantity(), dataToTest.get(i).getQuantity());
            Assert.assertEquals(orderItemList.get(i).getLineNumber(), dataToTest.get(i).getLineNumber());
        }
    }

    @Test
    public void testFindOrdersById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ")");
        Optional<Order> order = orderJdbcDao.getById(ORDER_ID);
        Assert.assertTrue(order.isPresent());
        Assert.assertEquals(ORDER_ID, order.get().getOrderId());
        Assert.assertEquals(RESTAURANT_ID, order.get().getRestaurant().getRestaurantId());
        Assert.assertEquals(USER_ID, order.get().getUser().getUserId());
        Assert.assertEquals(ORDER_TYPE, order.get().getOrderType());
        Assert.assertNotNull(order.get().getDateOrdered());
    }

    @Test
    public void testFindOrdersByUserId() throws SQLException {
        int iters = 10;
        for (int i = 0; i < iters; i++) {
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ")");
            for (int j = 0; j < i; j++)
                jdbcTemplate.execute("INSERT INTO order_items (order_id, product_id, line_number, quantity) VALUES (" + i + ", " + PRODUCT_ID + ", " + (j + 1) + ", " + (j + 2) + ")");
        }

        List<Order> orders = orderJdbcDao.getByUser(USER_ID, RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for (int i = 0; i < iters; i++) {
            Order order = orders.get(i);
            Assert.assertEquals(i, order.getItems().size());
            for (int j = 0; j < i; j++) {
                OrderItem item = order.getItems().get(j);
                Assert.assertEquals(PRODUCT_ID, item.getProduct().getProductId());
                Assert.assertEquals(j + 1, item.getLineNumber());
                Assert.assertEquals(j + 2, item.getQuantity());
            }

            Assert.assertEquals(i, order.getOrderId());
            Assert.assertEquals(RESTAURANT_ID, order.getRestaurant().getRestaurantId());
            Assert.assertEquals(USER_ID, order.getUser().getUserId());
            Assert.assertEquals(ORDER_TYPE, order.getOrderType());
            Assert.assertNotNull(order.getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByRestaurantId() throws SQLException {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ")");
        }

        List<Order> orders = orderJdbcDao.getByRestaurant(RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for (int i = 1; i <= iters; i++) {
            Assert.assertEquals(i, orders.get(i - 1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i - 1).getRestaurant().getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i - 1).getUser().getUserId());
            Assert.assertEquals(ORDER_TYPE, orders.get(i - 1).getOrderType());
            Assert.assertNotNull(orders.get(i - 1).getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByOrderTypeId() throws SQLException {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ")");
        }

        List<Order> orders = orderJdbcDao.getByOrderTypeAndRestaurant(ORDER_TYPE, RESTAURANT_ID);

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for (int i = 1; i <= iters; i++) {
            Assert.assertEquals(i, orders.get(i - 1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i - 1).getRestaurant().getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i - 1).getUser().getUserId());
            Assert.assertEquals(ORDER_TYPE, orders.get(i - 1).getOrderType());
            Assert.assertNotNull(orders.get(i - 1).getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByOrderDate() throws SQLException {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ")");
        }

        List<Order> orders = orderJdbcDao.getByRestaurantOrderedBetweenDates(RESTAURANT_ID, LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for (int i = 1; i <= iters; i++) {
            Assert.assertEquals(i, orders.get(i - 1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i - 1).getRestaurant().getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i - 1).getUser().getUserId());
            Assert.assertEquals(ORDER_TYPE, orders.get(i - 1).getOrderType());
            Assert.assertNotNull(orders.get(i - 1).getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByAddress() throws SQLException {
        long count = Arrays.stream(ADDRESSES).filter(str -> str.equals(ADDRESS)).count();

        for (int i = 1; i <= ADDRESSES.length; i++) {
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, address) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", '" + ADDRESSES[i - 1] + "')");
        }

        List<Order> orders = orderJdbcDao.getByRestaurantAndAddress(RESTAURANT_ID, ADDRESS);

        Assert.assertNotNull(orders);
        Assert.assertEquals(count, orders.size());
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(RESTAURANT_ID, orders.get(i).getRestaurant().getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i).getUser().getUserId());
            Assert.assertEquals(ORDER_TYPE, orders.get(i).getOrderType());
            Assert.assertNotNull(orders.get(i).getDateOrdered());
            Assert.assertEquals(ADDRESS, orders.get(i).getAddress());
        }
    }

    @Test
    public void testFindOrdersByTableNumber() throws SQLException {
        long count = Arrays.stream(TABLE_NUMBERS).filter(num -> num.equals(TABLE_NUMBER)).count();

        for (int i = 1; i < TABLE_NUMBERS.length + 1; i++) {
            jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, table_number) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", '" + TABLE_NUMBERS[i - 1] + "')");
        }

        List<Order> orders = orderJdbcDao.getByRestaurantAndTableNumber(RESTAURANT_ID, TABLE_NUMBER);

        Assert.assertNotNull(orders);
        Assert.assertEquals(count, orders.size());
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(RESTAURANT_ID, orders.get(i).getRestaurant().getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i).getUser().getUserId());
            Assert.assertEquals(ORDER_TYPE, orders.get(i).getOrderType());
            Assert.assertNotNull(orders.get(i).getDateOrdered());
            Assert.assertEquals(TABLE_NUMBER, orders.get(i).getTableNumber());
        }
    }

    @Test
    public void testUpdateAddress() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, address) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", '" + ADDRESS + "')");
        Assert.assertTrue(orderJdbcDao.updateAddress(ORDER_ID, "newAddress"));
    }

    @Test
    public void testUpdateTableNumber() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, table_number) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", '" + TABLE_NUMBER + "')");
        Assert.assertTrue(orderJdbcDao.updateTableNumber(ORDER_ID, TABLE_NUMBER + 10));
    }
}
