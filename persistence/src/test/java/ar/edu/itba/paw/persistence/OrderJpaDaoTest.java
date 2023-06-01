package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.util.PaginatedResult;
import net.bytebuddy.asm.Advice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class OrderJpaDaoTest {
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
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final boolean IS_ACTIVE = true;
    private static final String PREFERRED_LANGUAGE = "en";
    private static final OrderType ORDER_TYPE = OrderType.DINE_IN;
    private static final long PRODUCT_ID = 212;
    private static final String PRODUCT_NAME = "Pepinito con sal";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("533.55");
    private static final long CATEGORY_ID = 12421;
    private static final String CATEGORY_NAME = "Postgres Dulces";
    private static final int CATEGORY_ORDER = 10;
    private static final int SPECIALTY = 2;
    private static final String RESTAURANT_ADDRESS = "Calle 123";
    private static final String RESTAURANT_DESCRIPTION = "Restaurante de empanadas";
    private static final LocalDateTime RESTAURANT_CREATION_DATE = LocalDateTime.now();
    private static final boolean RESTAURANT_DELETED = false;
    private static final boolean RESTAURANT_IS_ACTIVE = true;
    private static final boolean CATEGORY_DELETED = false;
    private static final boolean PRODUCT_AVAILABLE = true;
    private static final String PRODUCT_DESCRIPTION = "Empanada de carne";


    private List<OrderItem> orderItemList;

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "restaurants", "users", "orders", "products", "categories", "order_items");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "', " + MAX_TABLES + ", " + SPECIALTY + ", " + USER_ID + ", '" + RESTAURANT_ADDRESS + "', '" + RESTAURANT_DESCRIPTION + "', '" + Timestamp.valueOf(RESTAURANT_CREATION_DATE) + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num, deleted) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "', " + ORDER_ID + ", " + CATEGORY_DELETED + ")");
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id, available, description, deleted) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ", " + PRODUCT_AVAILABLE + ", '" + PRODUCT_DESCRIPTION + "', false)");
    }
    private void createRandomItemListForOrder(Order order, int start, int finish) {
        orderItemList = new ArrayList<>();
        for (int i = start; i < finish; i++) {
            Product product = new Product(CATEGORY_ID, PRODUCT_NAME + i + 1, PRODUCT_DESCRIPTION, null, PRODUCT_PRICE.add(BigDecimal.valueOf(i + 1)), PRODUCT_AVAILABLE);
            em.persist(product);

            OrderItem orderItem = new OrderItem(product, i + 1, i + 1, null);
            orderItem.setOrderId(order.getOrderId());
            em.persist(orderItem);
            orderItemList.add(orderItem);
            em.flush();
        }
        order.getItems().addAll(orderItemList);
    }

    private void traverseOrderItemList(List<OrderItem> orderItemList, List<OrderItem> dataToTest) {
        boolean present = true;
        for (OrderItem orderItem : orderItemList) {
            Assert.assertTrue(present);
            present = false;
            for (OrderItem data : dataToTest) {
                if (Objects.equals(orderItem.getProduct().getProductId(), data.getProduct().getProductId())) {
                    Assert.assertEquals(orderItem.getComment(), data.getComment());
                    Assert.assertEquals(orderItem.getQuantity(), data.getQuantity());
                    Assert.assertEquals(orderItem.getLineNumber(), data.getLineNumber());
                    present = true;
                }
            }
        }
    }

    @Test
    public void testCreationDineIn() throws SQLException {
        Order order = orderDao.createDineIn(RESTAURANT_ID, USER_ID, TABLE_NUMBER);
        em.flush();

        createRandomItemListForOrder(order, 0, 10);
        List<OrderItem> dataToTest = order.getItems();

        Assert.assertEquals(OrderType.DINE_IN, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId());
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        traverseOrderItemList(orderItemList, dataToTest);
    }

    @Test
    public void testCreationTakeaway() throws SQLException {
        Order order = orderDao.createTakeaway(RESTAURANT_ID, USER_ID);
        em.flush();

        createRandomItemListForOrder(order, 0, 10);
        List<OrderItem> dataToTest = order.getItems();

        Assert.assertEquals(OrderType.TAKEAWAY, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId());
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        traverseOrderItemList(orderItemList, dataToTest);
    }

    @Test
    public void testCreationDelivery() throws SQLException {
        Order order = orderDao.createDelivery(RESTAURANT_ID, USER_ID, ADDRESS);
        em.flush();

        createRandomItemListForOrder(order, 0, 10);
        List<OrderItem> dataToTest = order.getItems();

        Assert.assertEquals(OrderType.DELIVERY, order.getOrderType());
        Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
        Assert.assertEquals(USER_ID, order.getUserId());
        Assert.assertEquals(ADDRESS, order.getAddress());
        Assert.assertEquals(orderItemList.size(), dataToTest.size());
        traverseOrderItemList(orderItemList, dataToTest);
    }

    @Test
    public void testFindOrdersById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id) VALUES (" + ORDER_ID + ", " + RESTAURANT_ID + ", " + ORDER_TYPE.ordinal() + ", '" + Timestamp.valueOf(LocalDateTime.now()) + "', " + USER_ID + ")");
        Optional<Order> order = orderDao.getById(ORDER_ID);

        Assert.assertTrue(order.isPresent());
        Assert.assertEquals(ORDER_ID, order.get().getOrderId());
        Assert.assertEquals(RESTAURANT_ID, order.get().getRestaurantId());
        Assert.assertEquals(USER_ID, order.get().getUserId());
        Assert.assertEquals(ORDER_TYPE, order.get().getOrderType());
        Assert.assertNotNull(order.get().getDateOrdered());
    }

    @Test
    public void testFindOrdersByUserId() throws SQLException {
        int iters = 5;
        int start = 0;
        int finish = 0;
        List<List<OrderItem>> orderItems = new ArrayList<>();
        for (int i = 0; i < iters; i++) {
            jdbcTemplate.update("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, date_ordered) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", ?)", Timestamp.valueOf(LocalDateTime.now().minusDays(i)));
            createRandomItemListForOrder(em.find(Order.class, (long) i), start, finish);
            start = finish;
            finish += i + 1;
            orderItems.add(orderItemList);
        }

        List<Order> orders = orderDao.getByUser(USER_ID, 1, iters).getResult();

        Assert.assertNotNull(orders);
        Assert.assertEquals(iters, orders.size());

        for (int i = 0; i < iters; i++) {
            Order order = orders.get(i);
            Assert.assertEquals(i * 2, order.getItems().size());
            Assert.assertEquals(i, order.getOrderId());
            Assert.assertEquals(RESTAURANT_ID, order.getRestaurantId());
            Assert.assertEquals(USER_ID, order.getUserId());
            Assert.assertEquals(ORDER_TYPE, order.getOrderType());
            Assert.assertNotNull(order.getDateOrdered());
            traverseOrderItemList(order.getItems(), orderItems.get(i));
        }
    }

    @Test
    public void testFindOrdersByRestaurantIdPagedWithNoOrderStatus() throws SQLException {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.update("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, date_ordered) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", ?)", Timestamp.valueOf(LocalDateTime.now().minusDays(i)));
        }

        int pageSize = 4;
        int pageCount = (iters + pageSize - 1) / pageSize;
        List<PaginatedResult<Order>> pages = new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            PaginatedResult<Order> page = orderDao.getByRestaurant(RESTAURANT_ID, i, pageSize, null);
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
            Assert.assertEquals(USER_ID, allOrders.get(i - 1).getUserId());
            Assert.assertEquals(ORDER_TYPE, allOrders.get(i - 1).getOrderType());
            Assert.assertNotNull(allOrders.get(i - 1).getDateOrdered());
        }
    }

    @Test
    public void testFindOrdersByRestaurantIdPagedWithOrderStatusConfirmed() {
        int iters = 10;
        for (int i = 1; i <= iters; i++) {
            jdbcTemplate.update("INSERT INTO orders (order_id, restaurant_id, user_id, order_type, date_ordered, date_confirmed) VALUES (" + i + ", " + RESTAURANT_ID + ", " + USER_ID + ", " + ORDER_TYPE.ordinal() + ", ?, ?)", Timestamp.valueOf(LocalDateTime.now().minusDays(i)), Timestamp.valueOf(LocalDateTime.now().minusDays(i)));
        }

        int pageSize = 4;
        int pageCount = (iters + pageSize - 1) / pageSize;
        List<PaginatedResult<Order>> pages = new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            PaginatedResult<Order> page = orderDao.getByRestaurant(RESTAURANT_ID, i, pageSize, OrderStatus.CONFIRMED);
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
            Assert.assertEquals(USER_ID, allOrders.get(i - 1).getUserId());
            Assert.assertEquals(ORDER_TYPE, allOrders.get(i - 1).getOrderType());
            Assert.assertNotNull(allOrders.get(i - 1).getDateOrdered());
            Assert.assertNotNull(allOrders.get(i - 1).getDateConfirmed());
        }
    }
}