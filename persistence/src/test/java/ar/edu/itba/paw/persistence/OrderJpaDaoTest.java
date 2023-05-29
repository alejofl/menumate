package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.util.PaginatedResult;
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class OrderJpaDaoTest {
// TODO: Fix tests

    private static final long ORDER_ID = 8844;
    private static final String ADDRESS = "Calle 123";
    private static final int TABLE_NUMBER = 11;
    private static final String[] ADDRESSES = {"Calle 123", "Calle 456", "Calle 789", "Calle 101112", "Calle 131415", "Calle 123"};
    private static final Integer[] TABLE_NUMBERS = {1, 2, 3, 4, 5, 6, 10, 10, 10, 11};
    private static final long RESTAURANT_ID = 45123;
    private static final String RESTAURANT_NAME = "EmpanadasMorita";
    private static final String RESTAURANT_EMAIL = "ivawashere@email.com";
    private static final int MAX_TABLES = 20;
    private static final long USER_ID = 791;
    private static final String EMAIL = "peter@peter.com";
    private static final String PASSWORD = "super12secret34";
    private static final String NAME = "Peter Parker";
    private static final OrderType ORDER_TYPE = OrderType.DINE_IN;
    private static final long PRODUCT_ID = 212;
    private static final String PRODUCT_NAME = "Pepinito con sal";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("533.55");
    private static final long CATEGORY_ID = 12421;
    private static final String CATEGORY_NAME = "Postgres Dulces";
    private static final int CATEGORY_ORDER = 10;
    private static final int SPECIALTY = 2;
    private List<OrderItem> orderItemList;

    @Autowired
    private DataSource ds;

    @Autowired
    private OrderJpaDao orderDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "restaurants", "users", "orders", "products", "categories", "order_items");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + EMAIL + "', '" + PASSWORD + "', '" + NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables, specialty) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "', " + USER_ID + ", " + MAX_TABLES + ", " + SPECIALTY + ")");
        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "', " + CATEGORY_ORDER + ")");
        jdbcTemplate.execute("INSERT INTO products(product_id, category_id, name, price) VALUES (" + PRODUCT_ID + ", " + CATEGORY_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ")");
        orderItemList = new ArrayList<>();
    }

    private void createRandomItemList(int orderType) {

        Order order = mock(Order.class);
        when(order.getOrderId()).thenReturn(ORDER_ID);
        when(order.getOrderType()).thenReturn(OrderType.fromOrdinal(orderType));
        when(order.getRestaurant()).thenReturn(mock(Restaurant.class));
        when(order.getRestaurantId()).thenReturn(RESTAURANT_ID);
        when(order.getUser()).thenReturn(mock(User.class));
        when(order.getUserId()).thenReturn(USER_ID);
        jdbcTemplate.execute("INSERT INTO orders(order_id, order_type, restaurant_id, user_id, address, table_number) VALUES (" + ORDER_ID + ", " + orderType + ", " + RESTAURANT_ID + ", " + USER_ID + ", '" + ADDRESS + "', " + TABLE_NUMBER + ")");

        Category category = mock(Category.class);
        when(category.getCategoryId()).thenReturn(CATEGORY_ID);
        when(category.getRestaurant()).thenReturn(mock(Restaurant.class));
        when(category.getRestaurantId()).thenReturn(RESTAURANT_ID);
        for (int i = 0; i < 10; i++) {
            long product_id = PRODUCT_ID + i + 1;
            long cateogry_id = CATEGORY_ID;
            String product_name = PRODUCT_NAME + i + 1;
            BigDecimal product_price = PRODUCT_PRICE.add(BigDecimal.valueOf(i + 1));

            jdbcTemplate.execute("INSERT INTO products(product_id, category_id, name, price) VALUES (" + product_id + ", " + cateogry_id + ", '" + product_name + "', " + product_price + ")");
            Product product = new Product(product_id, category, product_name, product_price, null, null, true, false);

            int line_number = i + 1;
            OrderItem orderItem = new OrderItem(product, line_number, 10, "comment");
            orderItemList.add(orderItem);
            jdbcTemplate.execute("INSERT INTO order_items(order_id, product_id, line_number, quantity, comment) VALUES (" + ORDER_ID + ", " + product_id + ", " + line_number + ", " + 10 + ", '" + "comment" + "')");
        }
    }

    @Test
    public void testCreationDineIn() throws SQLException {
        createRandomItemList(OrderType.DINE_IN.ordinal());
        Order order = orderDao.createDineIn(RESTAURANT_ID, USER_ID, TABLE_NUMBER, orderItemList);
        Assert.assertEquals(ORDER_TYPE, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId().longValue());

        // Testing ordered items
        List<OrderItem> dataToTest = order.getItems();
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        boolean present = true;
        for (OrderItem orderItem : orderItemList) {
            Assert.assertTrue(present);
            present = false;
            for (OrderItem data : dataToTest) {
                if (orderItem.getProduct().getProductId() == data.getProduct().getProductId()) {
                    Assert.assertEquals(orderItem.getComment(), data.getComment());
                    Assert.assertEquals(orderItem.getQuantity(), data.getQuantity());
                    Assert.assertEquals(orderItem.getLineNumber(), data.getLineNumber());
                    present = true;
                }
            }
        }
    }

    @Test
    public void testCreationTakeaway() throws SQLException {
        createRandomItemList(OrderType.TAKEAWAY.ordinal());
        Order order = orderDao.createTakeaway(RESTAURANT_ID, USER_ID, orderItemList);
        Assert.assertEquals(OrderType.TAKEAWAY, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId().longValue());

        // Testing ordered items
        List<OrderItem> dataToTest = order.getItems();
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        boolean present = true;
        for (OrderItem orderItem : orderItemList) {
            Assert.assertTrue(present);
            present = false;
            for (OrderItem data : dataToTest) {
                if (orderItem.getProduct().getProductId() == data.getProduct().getProductId()) {
                    Assert.assertEquals(orderItem.getComment(), data.getComment());
                    Assert.assertEquals(orderItem.getQuantity(), data.getQuantity());
                    Assert.assertEquals(orderItem.getLineNumber(), data.getLineNumber());
                    present = true;
                }
            }
        }
    }

    @Test
    public void testCreationDelivery() throws SQLException {
        createRandomItemList(OrderType.DELIVERY.ordinal());
        Order order = orderDao.createDelivery(RESTAURANT_ID, USER_ID, ADDRESS, orderItemList);
        Assert.assertEquals(OrderType.DELIVERY, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId().longValue());
        Assert.assertEquals(ADDRESS, order.getAddress());

        // Testing ordered items
        List<OrderItem> dataToTest = order.getItems();
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        boolean present = true;
        for (OrderItem orderItem : orderItemList) {
            Assert.assertTrue(present);
            present = false;
            for (OrderItem data : dataToTest) {
                if (orderItem.getProduct().getProductId() == data.getProduct().getProductId()) {
                    Assert.assertEquals(orderItem.getComment(), data.getComment());
                    Assert.assertEquals(orderItem.getQuantity(), data.getQuantity());
                    Assert.assertEquals(orderItem.getLineNumber(), data.getLineNumber());
                    present = true;
                }
            }
        }
    }

    @Test
    public void testFindOrdersById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ")");
        Optional<Order> order = orderDao.getById(ORDER_ID);
        Assert.assertTrue(order.isPresent());
        Assert.assertEquals(ORDER_ID, order.get().getOrderId());
        Assert.assertEquals(RESTAURANT_ID, order.get().getRestaurantId());
        Assert.assertEquals(USER_ID, order.get().getUserId().longValue());
        Assert.assertEquals(ORDER_TYPE, order.get().getOrderType());
        Assert.assertNotNull(order.get().getDateOrdered());
    }

    @Test
    public void testFindOrdersByUserId() throws SQLException {
        int iters = 10;
        for (int i = 0; i < iters; i++) {
            jdbcTemplate.update("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, date_ordered) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", ?)", Timestamp.valueOf(LocalDateTime.now().minusDays(i)));
            for (int j = 0; j < i; j++)
                jdbcTemplate.execute("INSERT INTO order_items (order_id, product_id, line_number, quantity) VALUES (" + i + ", " + PRODUCT_ID + ", " + (j + 1) + ", " + (j + 2) + ")");
        }

        List<Order> orders = orderDao.getByUser(USER_ID, 1, iters).getResult();

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
            Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
            Assert.assertEquals(USER_ID, order.getUserId().longValue());
            Assert.assertEquals(ORDER_TYPE, order.getOrderType());
            Assert.assertNotNull(order.getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByRestaurantId() throws SQLException {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.update("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, date_ordered) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", ?)", Timestamp.valueOf(LocalDateTime.now().minusDays(i)));
        }

        List<Order> orders = orderDao.getByRestaurant(RESTAURANT_ID, 1, iters).getResult();

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for (int i = 1; i <= iters; i++) {
            Assert.assertEquals(i, orders.get(i - 1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, orders.get(i - 1).getRestaurantId());
            Assert.assertEquals(USER_ID, orders.get(i - 1).getUserId().longValue());
            Assert.assertEquals(ORDER_TYPE, orders.get(i - 1).getOrderType());
            Assert.assertNotNull(orders.get(i - 1).getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByRestaurantIdPaged() throws SQLException {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.update("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, date_ordered) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", ?)", Timestamp.valueOf(LocalDateTime.now().minusDays(i)));
        }

        int pageSize = 4;
        int pageCount = (iters + pageSize - 1) / pageSize;
        List<PaginatedResult<Order>> pages = new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            PaginatedResult<Order> page = orderDao.getByRestaurant(RESTAURANT_ID, i, pageSize);
            pages.add(page);
        }

        List<Order> allOrders = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            PaginatedResult<Order> page = pages.get(i);
            Assert.assertNotNull(page);
            Assert.assertEquals(i + 1, page.getPageNumber());
            Assert.assertEquals(pageSize, page.getPageSize());
            allOrders.addAll(page.getResult());
        }

        for (int i = 1; i <= iters; i++) {
            Assert.assertEquals(i, allOrders.get(i - 1).getOrderId());
            Assert.assertEquals(RESTAURANT_ID, allOrders.get(i - 1).getRestaurantId());
            Assert.assertEquals(USER_ID, allOrders.get(i - 1).getUserId().longValue());
            Assert.assertEquals(ORDER_TYPE, allOrders.get(i - 1).getOrderType());
            Assert.assertNotNull(allOrders.get(i - 1).getDateOrdered());
        }
    }

    @Test
    public void testUpdateAddress() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, address) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + OrderType.DELIVERY.ordinal() + ", '" + ADDRESS + "')");
        orderDao.updateAddress(ORDER_ID, "newAddress");
    }

    @Test
    public void testUpdateTableNumber() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, table_number) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", '" + TABLE_NUMBER + "')");
        orderDao.updateTableNumber(ORDER_ID, TABLE_NUMBER + 10);
    }
}
    */