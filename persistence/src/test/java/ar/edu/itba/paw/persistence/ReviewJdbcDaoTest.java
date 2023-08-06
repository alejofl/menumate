package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.OrderConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.ReviewConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
import ar.edu.itba.paw.util.AverageCountPair;
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
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + OrderConstants.ORDER_ID_WITH_NO_REVIEW));
    }

    @Test
    @Rollback
    public void testCreateWhenExisting() {
        reviewDao.create(
                OrderConstants.ORDER_IDS_RESTAURANT_0[0],
                ReviewConstants.DEFAULT_REVIEW_RATING,
                null
        );
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + OrderConstants.ORDER_IDS_RESTAURANT_0[0]));
    }

    @Test
    public void testGetByOrderEmpty() {
        Optional<Review> review1 = reviewDao.getByOrder(OrderConstants.ORDER_ID_WITH_NO_REVIEW);
        Assert.assertFalse(review1.isPresent());
    }

    @Test
    public void testGetByOrderExisting() {
        Optional<Review> review1 = reviewDao.getByOrder(OrderConstants.ORDER_IDS_RESTAURANT_0[0]);

        Assert.assertTrue(review1.isPresent());
        Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[0], review1.get().getOrder().getOrderId().longValue());
        Assert.assertNotNull(review1.get().getComment());
        Assert.assertEquals(ReviewConstants.VALUES.get(0).get(0).intValue(), review1.get().getRating());
    }

    @Test
    public void testGetByRestaurantEmpty() {
        PaginatedResult<Review> result = reviewDao.get(null, RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS, 1, 1);
        Assert.assertEquals(0, result.getResult().size());
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByRestaurantExisting() {
        PaginatedResult<Review> result = reviewDao.get(null, RestaurantConstants.RESTAURANT_IDS[0], 1, OrderConstants.ORDER_IDS_RESTAURANT_0.length);
        Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0.length, result.getResult().size());
        Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0.length, result.getTotalCount());
    }

    @Test
    public void testReviewsByRestaurant() {
        final int totalOrders = OrderConstants.ORDER_IDS_RESTAURANT_0.length;
        PaginatedResult<Review> result = reviewDao.get(null, RestaurantConstants.RESTAURANT_IDS[0], 1, totalOrders);
        for (int i = 0; i < totalOrders; i++) {
            Review review = result.getResult().get(i);
            Assert.assertEquals(OrderConstants.ORDER_IDS_RESTAURANT_0[totalOrders - i - 1], review.getOrder().getOrderId().longValue());
            Assert.assertNotNull(review.getComment());
            Assert.assertEquals(ReviewConstants.VALUES.get(0).get(totalOrders - i - 1).intValue(), review.getRating());
        }
    }

    @Test
    public void testGetByUserEmpty() {
        PaginatedResult<Review> result = reviewDao.get(UserConstants.INACTIVE_USER_ID, null, 1, 20);
        Assert.assertEquals(0, result.getResult().size());
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByUserNotEmpty() {
        PaginatedResult<Review> result = reviewDao.get(UserConstants.ACTIVE_USER_ID, null, 1, OrderConstants.TOTAL_ORDER_COUNT);
        Assert.assertEquals(OrderConstants.TOTAL_ORDER_COUNT, result.getResult().size());
    }

    @Test
    public void testReviewsByUser() {
        PaginatedResult<Review> result = reviewDao.get(UserConstants.ACTIVE_USER_ID, null, 1, OrderConstants.TOTAL_ORDER_COUNT);
        Assert.assertEquals(OrderConstants.TOTAL_ORDER_COUNT, result.getResult().size());
        for (int i = 0; i < OrderConstants.TOTAL_ORDER_COUNT; i++) {
            Review review = result.getResult().get(i);

            Assert.assertTrue(
                    Arrays.stream(OrderConstants.ORDER_IDS_RESTAURANT_0).anyMatch(id -> id == review.getOrder().getOrderId()) ||
                            Arrays.stream(OrderConstants.ORDER_IDS_RESTAURANT_1).anyMatch(id -> id == review.getOrder().getOrderId().longValue()) ||
                            Arrays.stream(OrderConstants.ORDER_IDS_RESTAURANT_2).anyMatch(id -> id == review.getOrder().getOrderId().longValue())
            );

            Assert.assertNotNull(review.getComment());
        }
    }

    @Test
    public void testAverageByRestaurant() {
        AverageCountPair ac = reviewDao.getRestaurantAverage(RestaurantConstants.RESTAURANT_IDS[0]);
        Assert.assertEquals(ReviewConstants.AVERAGE_LIST.get(0), ac.getAverage(), 0.01);
        Assert.assertEquals(ReviewConstants.VALUES.get(0).size(), ac.getCount());
    }

    @Test
    public void testAverageByRestaurantWithNoOrders() {
        AverageCountPair ac = reviewDao.getRestaurantAverage(RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS);
        Assert.assertEquals(0, ac.getAverage(), 0.01);
        Assert.assertEquals(0, ac.getCount());
    }

    @Test
    public void testAverageSinceWhenNone() {
        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS, ReviewConstants.DEFAULT_SINCE_WHEN);
        Assert.assertEquals(0, ac.getCount());
        Assert.assertEquals(0, ac.getAverage(), 0.1);
    }

    @Test
    public void testAverageSinceWithOne() {
        final List<Integer> averageList = ReviewConstants.VALUES.get(0);
        final int value = averageList.size() - 1;

        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RestaurantConstants.RESTAURANT_IDS[0], LocalDateTime.now().plusDays(value));
        Assert.assertEquals(1, ac.getCount());
        Assert.assertEquals(averageList.get(value), ac.getAverage(), 0.01);
    }

    @Test
    public void testAverageSinceWithTwo() {
        final List<Integer> averageList = ReviewConstants.VALUES.get(0);
        final int value = averageList.size() - 2;

        AverageCountPair ac = reviewDao.getRestaurantAverageSince(RestaurantConstants.RESTAURANT_IDS[0], LocalDateTime.now().plusDays(value));
        Assert.assertEquals(2, ac.getCount());
        Assert.assertEquals((averageList.get(value) + averageList.get(value + 1)) / 2f, ac.getAverage(), 0.01);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteWhenNone() {
        reviewDao.delete(OrderConstants.ORDER_ID_WITH_NO_REVIEW);
    }

    @Test
    @Rollback
    public void testDeleteWhenExists() {
        reviewDao.delete(OrderConstants.ORDER_IDS_RESTAURANT_0[0]);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "order_reviews", "order_id = " + OrderConstants.ORDER_IDS_RESTAURANT_0[0]));
    }
}
