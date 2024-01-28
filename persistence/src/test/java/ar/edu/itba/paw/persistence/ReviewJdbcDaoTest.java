package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.OrderConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.ReviewConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
import ar.edu.itba.paw.util.AverageCountPair;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ReviewJdbcDaoTest {

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
    }

    @Test
    @Rollback
    public void testCreateReview() {
        reviewDao.create(
                OrderConstants.ORDER_ID_WITH_NO_REVIEW,
                ReviewConstants.DEFAULT_REVIEW_RATING,
                ReviewConstants.DEFAULT_REVIEW_COMMENT
        );
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + OrderConstants.ORDER_ID_WITH_NO_REVIEW));
    }

    @Test(expected = InvalidUserArgumentException.class)
    @Rollback
    public void testCreateWhenExisting() {
        reviewDao.create(
                OrderConstants.ORDER_IDS_RESTAURANT_0[0],
                ReviewConstants.DEFAULT_REVIEW_RATING,
                null
        );
        em.flush();
    }

    @Test
    public void testGetByOrderEmpty() {
        final Optional<Review> review1 = reviewDao.getByOrder(OrderConstants.ORDER_ID_WITH_NO_REVIEW);
        assertFalse(review1.isPresent());
    }

    @Test
    public void testGetByOrderExisting() {
        final Optional<Review> review1 = reviewDao.getByOrder(OrderConstants.ORDER_IDS_RESTAURANT_0[0]);

        assertTrue(review1.isPresent());
        assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[0], review1.get().getOrder().getOrderId().longValue());
        assertNotNull(review1.get().getComment());
        assertEquals(ReviewConstants.VALUES.get(0).get(0).intValue(), review1.get().getRating());
    }

    @Test
    public void testGetByRestaurantEmpty() {
        final PaginatedResult<Review> result = reviewDao.get(null, RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS, 1, 1);
        assertEquals(0, result.getResult().size());
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByRestaurantExisting() {
        final PaginatedResult<Review> result = reviewDao.get(null, RestaurantConstants.RESTAURANT_IDS[0], 1, OrderConstants.ORDER_IDS_RESTAURANT_0.length);
        assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0.length, result.getResult().size());
        assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0.length, result.getTotalCount());
    }

    @Test
    public void testReviewsByRestaurant() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_0.length;
        final PaginatedResult<Review> result = reviewDao.get(null, RestaurantConstants.RESTAURANT_IDS[0], 1, totalOrders);
        for (int i = 0; i < totalOrders; i++) {
            Review review = result.getResult().get(i);
            assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[totalOrders - i - 1], review.getOrder().getOrderId().longValue());
            assertNotNull(review.getComment());
            assertEquals(ReviewConstants.VALUES.get(0).get(totalOrders - i - 1).intValue(), review.getRating());
        }
    }

    @Test
    public void testGetByUserEmpty() {
        final PaginatedResult<Review> result = reviewDao.get(UserConstants.INACTIVE_USER_ID, null, 1, 20);
        assertEquals(0, result.getResult().size());
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByUserNotEmpty() {
        final PaginatedResult<Review> result = reviewDao.get(UserConstants.ACTIVE_USER_ID, null, 1, OrderConstants.TOTAL_ORDER_COUNT);
        assertEquals(OrderConstants.TOTAL_ORDER_COUNT, result.getResult().size());
    }

    @Test
    public void testReviewsByUser() {
        final PaginatedResult<Review> result = reviewDao.get(UserConstants.ACTIVE_USER_ID, null, 1, OrderConstants.TOTAL_ORDER_COUNT);
        assertEquals(OrderConstants.TOTAL_ORDER_COUNT, result.getResult().size());
        for (int i = 0; i < OrderConstants.TOTAL_ORDER_COUNT; i++) {
            Review review = result.getResult().get(i);

            assertTrue(
                    Arrays.stream(OrderConstants.ORDER_IDS_RESTAURANT_0).anyMatch(id -> id == review.getOrder().getOrderId()) ||
                            Arrays.stream(OrderConstants.ORDER_IDS_RESTAURANT_1).anyMatch(id -> id == review.getOrder().getOrderId().longValue()) ||
                            Arrays.stream(OrderConstants.ORDER_IDS_RESTAURANT_2).anyMatch(id -> id == review.getOrder().getOrderId().longValue())
            );

            assertNotNull(review.getComment());
        }
    }

    @Test
    public void testAverageByRestaurant() {
        final AverageCountPair ac = reviewDao.getRestaurantAverage(RestaurantConstants.RESTAURANT_IDS[0]);
        assertEquals(ReviewConstants.AVERAGE_LIST.get(0), ac.getAverage(), 0.01);
        assertEquals(ReviewConstants.VALUES.get(0).size(), ac.getCount());
    }

    @Test
    public void testAverageByRestaurantWithNoOrders() {
        final AverageCountPair ac = reviewDao.getRestaurantAverage(RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS);
        assertEquals(0, ac.getAverage(), 0.01);
        assertEquals(0, ac.getCount());
    }

    @Test
    public void testAverageSinceWhenNone() {
        final AverageCountPair ac = reviewDao.getRestaurantAverageSince(RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS, ReviewConstants.DEFAULT_SINCE_WHEN);
        assertEquals(0, ac.getCount());
        assertEquals(0, ac.getAverage(), 0.1);
    }

    @Test
    public void testAverageSinceWithOne() {
        final List<Integer> averageList = ReviewConstants.VALUES.get(0);
        final int value = averageList.size() - 1;

        final AverageCountPair ac = reviewDao.getRestaurantAverageSince(RestaurantConstants.RESTAURANT_IDS[0], LocalDateTime.now().plusDays(value));
        assertEquals(1, ac.getCount());
        assertEquals(averageList.get(value), ac.getAverage(), 0.01);
    }

    @Test
    public void testAverageSinceWithTwo() {
        final List<Integer> averageList = ReviewConstants.VALUES.get(0);
        final int value = averageList.size() - 2;

        final AverageCountPair ac = reviewDao.getRestaurantAverageSince(RestaurantConstants.RESTAURANT_IDS[0], LocalDateTime.now().plusDays(value));
        assertEquals(2, ac.getCount());
        assertEquals((averageList.get(value) + averageList.get(value + 1)) / 2f, ac.getAverage(), 0.01);
    }

    @Test(expected = ReviewNotFoundException.class)
    public void testDeleteWhenNone() {
        reviewDao.delete(OrderConstants.ORDER_ID_WITH_NO_REVIEW);
    }

    @Test
    @Rollback
    public void testDeleteWhenExists() {
        reviewDao.delete(OrderConstants.ORDER_IDS_RESTAURANT_0[0]);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + OrderConstants.ORDER_IDS_RESTAURANT_0[0]));
    }
}
