package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.util.AverageCountPair;
import ar.edu.itba.paw.model.util.PaginatedResult;
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
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReviewJdbcDaoTest {
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final long OWNER_ID = 313;
    private static final String OWNER_EMAIL = "pedro@pedro.com";
    private static final String OWNER_PASSWORD = "mega12secreto34";
    private static final String OWNER_NAME = "Pedro Parker";
    private static final long RESTAURANT_ID1 = 5123;
    private static final String RESTAURANT_NAME1 = "pedros";
    private static final String RESTAURANT_EMAIL1 = "pedros@frompedros.com";
    private static final long RESTAURANT_ID2 = 4242;
    private static final String RESTAURANT_NAME2 = "La Mejor Pizza";
    private static final String RESTAURANT_EMAIL2 = "pizzeria@pizza.com";
    private static final int MAX_TABLES = 20;
    private static final long USER_ID_NONE = 1234;
    private static final long RESTAURANT_ID_NONE = 1234;
    private static final long ORDER_ID1 = 8844;
    private static final long ORDER_ID2 = 9090;
    private static final int RATING1 = 1;
    private static final int RATING2 = 4;
    private static final OrderType ORDER_TYPE = OrderType.TAKEAWAY;
    private static final float FLOAT_DELTA = 0.001f;

    @Autowired
    private DataSource ds;

    @Autowired
    private ReviewJdbcDao reviewDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "restaurants", "restaurant_roles");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + OWNER_ID + ", '" + OWNER_EMAIL + "', '" + OWNER_PASSWORD + "', '" + OWNER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables) VALUES (" + RESTAURANT_ID1 + ", '" + RESTAURANT_NAME1 + "', '" + RESTAURANT_EMAIL1 + "', " + OWNER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables) VALUES (" + RESTAURANT_ID2 + ", '" + RESTAURANT_NAME2 + "', '" + RESTAURANT_EMAIL2 + "', " + OWNER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + ORDER_ID1 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID1 + ", " + USER_ID + ", now(), now(), now(), now())");
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + ORDER_ID2 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID2 + ", " + USER_ID + ", now(), now(), now(), now())");
    }

    @Test
    public void testGetByOrderEmpty() {
        Optional<Review> review1 = reviewDao.getByOrder(ORDER_ID1);
        Assert.assertFalse(review1.isPresent());
    }

    @Test
    public void testGetByOrderBothEmpty() {
        Optional<Review> review1 = reviewDao.getByOrder(ORDER_ID1);
        Optional<Review> review2 = reviewDao.getByOrder(ORDER_ID2);
        Assert.assertFalse(review1.isPresent());
        Assert.assertFalse(review2.isPresent());
    }

    @Test
    public void testGetByOrderExisting() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        Optional<Review> review1 = reviewDao.getByOrder(ORDER_ID1);

        Assert.assertTrue(review1.isPresent());
        Assert.assertEquals(ORDER_ID1, review1.get().getOrder().getOrderId());
        Assert.assertEquals(RATING1, review1.get().getRating());
        Assert.assertNull(review1.get().getComment());
    }

    @Test
    public void testGetByOrderOneExistingOneNot() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        Optional<Review> review1 = reviewDao.getByOrder(ORDER_ID1);
        Optional<Review> review2 = reviewDao.getByOrder(ORDER_ID2);

        Assert.assertTrue(review1.isPresent());
        Assert.assertEquals(ORDER_ID1, review1.get().getOrder().getOrderId());
        Assert.assertEquals(RATING1, review1.get().getRating());
        Assert.assertNull(review1.get().getComment());
        Assert.assertFalse(review2.isPresent());
    }

    @Test
    public void testGetByOrderBothExisting() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID2 + ", " + RATING2 + ", null)");

        Optional<Review> review1 = reviewDao.getByOrder(ORDER_ID1);
        Optional<Review> review2 = reviewDao.getByOrder(ORDER_ID2);

        Assert.assertTrue(review1.isPresent());
        Assert.assertEquals(ORDER_ID1, review1.get().getOrder().getOrderId());
        Assert.assertEquals(RATING1, review1.get().getRating());
        Assert.assertNull(review1.get().getComment());
        Assert.assertTrue(review2.isPresent());
        Assert.assertEquals(ORDER_ID2, review2.get().getOrder().getOrderId());
        Assert.assertEquals(RATING2, review2.get().getRating());
        Assert.assertNull(review2.get().getComment());
    }

    @Test
    public void testGetByRestaurantEmpty() {
        PaginatedResult<Review> result = reviewDao.getByRestaurant(RESTAURANT_ID1, 1, 20);
        Assert.assertEquals(0, result.getResult().size());
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByRestaurantNotEmpty() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        PaginatedResult<Review> result = reviewDao.getByRestaurant(RESTAURANT_ID1, 1, 20);
        Assert.assertEquals(result.getResult().size(), 1);
        Review review = result.getResult().get(0);
        Assert.assertEquals(ORDER_ID1, review.getOrder().getOrderId());
        Assert.assertEquals(RATING1, review.getRating());
        Assert.assertNull(review.getComment());
    }

    @Test
    public void testGetByRestaurantOneEmptyOneNot() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID2 + ", " + RATING2 + ", null)");

        PaginatedResult<Review> result2 = reviewDao.getByRestaurant(RESTAURANT_ID2, 1, 20);
        Assert.assertEquals(1, result2.getResult().size());
        Assert.assertEquals(1, result2.getTotalCount());
        Review review = result2.getResult().get(0);
        Assert.assertEquals(ORDER_ID2, review.getOrder().getOrderId());
        Assert.assertEquals(RATING2, review.getRating());
        Assert.assertNull(review.getComment());

        PaginatedResult<Review> result1 = reviewDao.getByRestaurant(RESTAURANT_ID1, 1, 20);
        Assert.assertEquals(0, result1.getResult().size());
        Assert.assertEquals(0, result1.getTotalCount());
    }

    @Test
    public void testGetByRestaurantBothNotEmpty() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID2 + ", " + RATING2 + ", null)");

        PaginatedResult<Review> result1 = reviewDao.getByRestaurant(RESTAURANT_ID1, 1, 20);
        Assert.assertEquals(1, result1.getResult().size());
        Assert.assertEquals(1, result1.getTotalCount());
        Review review1 = result1.getResult().get(0);
        Assert.assertEquals(ORDER_ID1, review1.getOrder().getOrderId());
        Assert.assertEquals(RATING1, review1.getRating());
        Assert.assertNull(review1.getComment());

        PaginatedResult<Review> result2 = reviewDao.getByRestaurant(RESTAURANT_ID2, 1, 20);
        Assert.assertEquals(1, result2.getResult().size());
        Assert.assertEquals(1, result2.getTotalCount());
        Review review2 = result2.getResult().get(0);
        Assert.assertEquals(ORDER_ID2, review2.getOrder().getOrderId());
        Assert.assertEquals(RATING2, review2.getRating());
        Assert.assertNull(review2.getComment());
    }

    @Test
    public void testGetByUserEmpty() {
        PaginatedResult<Review> result = reviewDao.getByUser(USER_ID, 1, 20);
        Assert.assertEquals(0, result.getResult().size());
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByUserNotEmpty() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        PaginatedResult<Review> result = reviewDao.getByUser(USER_ID, 1, 20);
        Assert.assertEquals(result.getResult().size(), 1);
        Review review = result.getResult().get(0);
        Assert.assertEquals(ORDER_ID1, review.getOrder().getOrderId());
        Assert.assertEquals(RATING1, review.getRating());
        Assert.assertNull(review.getComment());
    }

    @Test
    public void testAverageZeroWhenEmpty() {
        AverageCountPair ac = reviewDao.getRestaurantAverage(RESTAURANT_ID1);
        Assert.assertEquals(0, ac.getCount());
    }

    @Test
    public void testAverageWhenOne() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        AverageCountPair ac = reviewDao.getRestaurantAverage(RESTAURANT_ID1);
        Assert.assertEquals(RATING1, ac.getAverage(), FLOAT_DELTA);
        Assert.assertEquals(1, ac.getCount());
    }

    @Test
    public void testAverageWhenTwo() {
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + 7381 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID1 + ", " + USER_ID + ", now(), now(), now(), now())");
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + 7381 + ", " + RATING2 + ", null)");

        AverageCountPair ac = reviewDao.getRestaurantAverage(RESTAURANT_ID1);
        Assert.assertEquals((RATING1 + RATING2) / 2f, ac.getAverage(), FLOAT_DELTA);
        Assert.assertEquals(2, ac.getCount());
    }

    @Test
    public void testAverageSinceWhenOne() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RESTAURANT_ID1, LocalDateTime.now().minusDays(1));
        Assert.assertEquals(RATING1, ac.getAverage(), FLOAT_DELTA);
        Assert.assertEquals(1, ac.getCount());
    }

    @Test
    public void testAverageSinceWhenTwo() {
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + 7381 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID1 + ", " + USER_ID + ", now(), now(), now(), now())");
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + 7381 + ", " + RATING2 + ", null)");

        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RESTAURANT_ID1, LocalDateTime.now().minusDays(1));
        Assert.assertEquals((RATING1 + RATING2) / 2f, ac.getAverage(), FLOAT_DELTA);
        Assert.assertEquals(2, ac.getCount());
    }

    @Test
    public void testDeleteWhenNone() {
        boolean success = reviewDao.delete(ORDER_ID1);
        Assert.assertFalse(success);
    }

    @Test
    public void testDeleteWhenExists() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        boolean success = reviewDao.delete(ORDER_ID1);

        Assert.assertTrue(success);
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + ORDER_ID1));
    }
}
