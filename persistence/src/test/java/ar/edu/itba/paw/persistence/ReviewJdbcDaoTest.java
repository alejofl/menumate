package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.util.AverageCountPair;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ReviewJdbcDaoTest {
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final boolean USER_IS_ACTIVE = true;
    private static final String USER_PREFERRED_LANGUAGE = "qx";
    private static final long OWNER_ID = 313;
    private static final String OWNER_EMAIL = "pedro@pedro.com";
    private static final String OWNER_PASSWORD = "mega12secreto34";
    private static final String OWNER_NAME = "Pedro Parker";
    private static final long RESTAURANT_ID1 = 5123;
    private static final long RESTAURANT_SPECIALITY = 0;
    private static final String RESTAURANT_NAME1 = "pedros";
    private static final String RESTAURANT_EMAIL1 = "pedros@frompedros.com";
    private static final long RESTAURANT_ID2 = 4242;
    private static final String RESTAURANT_NAME2 = "La Mejor Pizza";
    private static final String RESTAURANT_EMAIL2 = "pizzeria@pizza.com";
    private static final boolean RESTAURANT_DELETED = false;
    private static final boolean RESTAURANT_IS_ACTIVE = true;
    private static final String RESTAURANT_ADDRESS = "Av. Siempreviva 742";
    private static final String RESTAURANT_DESCRIPTION = "La mejor pizza de la ciudad";
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
    private ReviewJpaDao reviewDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "restaurants", "restaurant_roles");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "', " + USER_IS_ACTIVE + ", '" + USER_PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + OWNER_ID + ", '" + OWNER_EMAIL + "', '" + OWNER_PASSWORD + "', '" + OWNER_NAME + "', " + USER_IS_ACTIVE + ", '" + USER_PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID1 + ", '" + RESTAURANT_NAME1 + "', '" + RESTAURANT_EMAIL1 + "', " + MAX_TABLES + ", " + RESTAURANT_SPECIALITY + ", " + USER_ID + ", '" + RESTAURANT_ADDRESS + "', '" + RESTAURANT_DESCRIPTION + "', '" + Timestamp.valueOf(LocalDateTime.now()) + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID2 + ", '" + RESTAURANT_NAME2 + "', '" + RESTAURANT_EMAIL2 + "', " + MAX_TABLES + ", " + RESTAURANT_SPECIALITY + ", " + USER_ID + ", '" + RESTAURANT_ADDRESS + "', '" + RESTAURANT_DESCRIPTION + "', '" + Timestamp.valueOf(LocalDateTime.now()) + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + ORDER_ID1 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID1 + ", " + USER_ID + ", now(), now(), now(), now())");
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + ORDER_ID2 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID2 + ", " + USER_ID + ", now(), now(), now(), now())");
    }

    @Test
    public void testCreateWhenNonExisting() {
        reviewDao.create(ORDER_ID1, RATING1, null);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id=" + ORDER_ID1 + " AND rating=" + RATING1 + " AND comment IS NULL"));
    }

    @Test
    public void testCreateWhenExisting() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
        reviewDao.create(ORDER_ID1, RATING1, null);
        Assert.assertThrows(PersistenceException.class, () -> em.flush());
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

//    @Test
//    public void testAverageSinceWhenOne() {
//        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
//
//        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RESTAURANT_ID1, LocalDateTime.now().minusDays(1));
//        Assert.assertEquals(1, ac.getCount());
//        Assert.assertEquals(RATING1, ac.getAverage(), FLOAT_DELTA);
//    }
//
//    @Test
//    public void testAverageSinceWhenTwo() {
//        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + 7381 + ", " + ORDER_TYPE.ordinal() + ", " + RESTAURANT_ID1 + ", " + USER_ID + ", now(), now(), now(), now())");
//        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");
//        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + 7381 + ", " + RATING2 + ", null)");
//
//        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RESTAURANT_ID1, LocalDateTime.now().minusDays(1));
//        Assert.assertEquals((RATING1 + RATING2) / 2f, ac.getAverage(), FLOAT_DELTA);
//        Assert.assertEquals(2, ac.getCount());
//    }

    @Test
    public void testDeleteWhenNone() {
        Assert.assertThrows(EntityNotFoundException.class, () -> reviewDao.delete(ORDER_ID1));
    }

    @Test
    public void testDeleteWhenExists() {
        jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + ORDER_ID1 + ", " + RATING1 + ", null)");

        reviewDao.delete(ORDER_ID1);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + ORDER_ID1));
    }
}
