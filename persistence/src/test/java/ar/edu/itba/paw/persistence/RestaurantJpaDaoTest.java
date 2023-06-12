package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.ProductConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.ReviewConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
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
import java.sql.SQLException;
import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class RestaurantJpaDaoTest {
    private static final long NON_EXISTENT_RESTAURANT_ID = 5000;
    private static final String NON_EXISTENT_RESTAURANT_NAME = "Nonexistent Restaurant";
    private static final String NON_EXISTENT_RESTAURANT_EMAIL = "nonexistent@restaurant.com";

    @Autowired
    private DataSource ds;

    @Autowired
    private RestaurantJpaDao restaurantDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<Restaurant> maybeRestaurant = restaurantDao.getById(RestaurantConstants.RESTAURANT_IDS[0]);

        Assert.assertTrue(maybeRestaurant.isPresent());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], maybeRestaurant.get().getRestaurantId());
        Assert.assertEquals(UserConstants.RESTAURANT_OWNER_ID, maybeRestaurant.get().getOwnerUserId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_NAMES[0], maybeRestaurant.get().getName());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_EMAIL, maybeRestaurant.get().getEmail());
        Assert.assertEquals(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0).ordinal(), maybeRestaurant.get().getSpecialty().ordinal());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_ADDRESS, maybeRestaurant.get().getAddress());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_DESCRIPTION, maybeRestaurant.get().getDescription());
        Assert.assertEquals(RestaurantConstants.MAX_TABLES, maybeRestaurant.get().getMaxTables());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IS_ACTIVE, maybeRestaurant.get().getIsActive());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_DELETED, maybeRestaurant.get().getDeleted());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_CREATION_DATE.getDayOfYear(), maybeRestaurant.get().getDateCreated().getDayOfYear());
    }

    @Test
    public void testFindByIdNotFound() throws SQLException {
        Optional<Restaurant> maybeRestaurant = restaurantDao.getById(NON_EXISTENT_RESTAURANT_ID);

        Assert.assertFalse(maybeRestaurant.isPresent());
    }

    @Test
    @Rollback
    public void testCreation() throws SQLException {
        Restaurant restaurant = restaurantDao.create(
                NON_EXISTENT_RESTAURANT_NAME,
                NON_EXISTENT_RESTAURANT_EMAIL,
                RestaurantConstants.RESTAURANTS_SPECIALITY.get(0),
                UserConstants.RESTAURANT_OWNER_ID,
                RestaurantConstants.RESTAURANT_ADDRESS,
                RestaurantConstants.RESTAURANT_DESCRIPTION,
                RestaurantConstants.MAX_TABLES,
                null, null, null, true, null
        );
        em.flush();

        Assert.assertNotNull(restaurant);
        Assert.assertEquals(NON_EXISTENT_RESTAURANT_NAME, restaurant.getName());
        Assert.assertEquals(NON_EXISTENT_RESTAURANT_EMAIL, restaurant.getEmail());
        Assert.assertEquals(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0).ordinal(), restaurant.getSpecialty().ordinal());
        Assert.assertEquals(UserConstants.RESTAURANT_OWNER_ID, restaurant.getOwnerUserId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_ADDRESS, restaurant.getAddress());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_DESCRIPTION, restaurant.getDescription());
        Assert.assertEquals(RestaurantConstants.MAX_TABLES, restaurant.getMaxTables());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IS_ACTIVE, restaurant.getIsActive());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_DELETED, restaurant.getDeleted());
    }

    @Test
    @Rollback
    public void testDeletion() throws SQLException {
        restaurantDao.delete(RestaurantConstants.RESTAURANT_IDS[0]);
        em.flush();

        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS.length - 1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurants", "deleted = false"));
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurants", "deleted = true"));
    }

    @Test
    public void searchEmpty() throws SQLException {
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(null, 1, RestaurantConstants.RESTAURANT_IDS.length, null, false, null, null);
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS.length, res.getResult().size());
    }

    @Test
    public void searchSortedByNameAsc() throws SQLException {
        PaginatedResult<RestaurantDetails> restaurantsDetails = restaurantDao.search(
                "", 1,
                RestaurantConstants.RESTAURANT_IDS.length,
                RestaurantOrderBy.ALPHABETIC,
                false, null, null
        );

        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS.length, restaurantsDetails.getTotalCount());
        int j = 0;
        for (RestaurantDetails rd : restaurantsDetails.getResult()) {
            Assert.assertEquals(Character.toString((char) ('A' + j)), rd.getRestaurant().getName());
            j++;
        }
    }

    @Test
    public void searchSortedByNameDesc() throws SQLException {
        PaginatedResult<RestaurantDetails> restaurantsDetails = restaurantDao.search(
                "", 1,
                RestaurantConstants.RESTAURANT_IDS.length,
                RestaurantOrderBy.ALPHABETIC,
                true, null, null
        );
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS.length, restaurantsDetails.getTotalCount());

        int j = RestaurantConstants.RESTAURANT_IDS.length - 1;
        for (RestaurantDetails rd : restaurantsDetails.getResult()) {
            Assert.assertEquals(Character.toString((char) ('A' + j)), rd.getRestaurant().getName());
            j--;
        }
    }

    @Test
    public void searchSortedByRatingAsc() throws SQLException {
        final List<Double> ratingAvg = ReviewConstants.AVERAGE_LIST;
        ratingAvg.sort(Comparator.naturalOrder());

        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(
                "", 1, maxRestaurants,
                RestaurantOrderBy.RATING,
                false, null, null
        );

        int size = res.getResult().size();
        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(maxRestaurants, res.getTotalCount());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[3].longValue(), res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(maxRestaurants-1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(ratingAvg.get(i).intValue(), res.getResult().get(i).getAverageRating());
        }
    }

    @Test
    public void searchSortedByRatingDesc() throws SQLException {
        final List<Double> ratingAvg = ReviewConstants.AVERAGE_LIST;
        ratingAvg.sort(Comparator.reverseOrder());

        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(
                "", 1, maxRestaurants,
                RestaurantOrderBy.RATING,
                true, null, null
        );

        int size = res.getResult().size();
        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(maxRestaurants, res.getTotalCount());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[3].longValue(), res.getResult().get(maxRestaurants-1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(ratingAvg.get(i).intValue(), res.getResult().get(i).getAverageRating());
        }
    }

    @Test
    public void searchSortedByPriceAverageDesc() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<Double> avgPrice = ProductConstants.AVERAGE_LIST;
        avgPrice.sort(Comparator.reverseOrder());

        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.PRICE, true, null, null);

        int size = res.getResult().size();
        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(maxRestaurants, res.getTotalCount());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[1].longValue(), res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[2].longValue(), res.getResult().get(maxRestaurants-1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(avgPrice.get(i),res.getResult().get(i).getAverageProductPrice(), 0.1);
        }
    }

    @Test
    public void searchSortedByPriceAverageAsc() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<Double> avgPrice = ProductConstants.AVERAGE_LIST;
        avgPrice.sort(Comparator.naturalOrder());

        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.PRICE, false, null, null);

        int size = res.getResult().size();
        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(maxRestaurants, res.getTotalCount());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[2].longValue(), res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[1].longValue(), res.getResult().get(maxRestaurants - 1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(avgPrice.get(i),res.getResult().get(i).getAverageProductPrice(), 0.1);
        }
    }

    @Test
    public void searchSortedByCreationDateAsc() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RestaurantConstants.RESTAURANT_IDS.length, RestaurantOrderBy.DATE, false, null, null);
        Assert.assertEquals(maxRestaurants, res.getResult().size());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[maxRestaurants-1].longValue(), res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(maxRestaurants-1).getRestaurantId());
    }

    @Test
    public void getSortedByCreationDateDesc() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.DATE, true, null, null);
        Assert.assertEquals(maxRestaurants, res.getResult().size());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[maxRestaurants-1].longValue(), res.getResult().get(maxRestaurants-1).getRestaurantId());
    }

    @Test
    @Rollback
    public void searchByRestaurantName() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final String restaurantName = RestaurantConstants.RESTAURANT_NAMES[0];
        for(int i=0; i<maxRestaurants ; i++){
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }

        PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, null, null);
        Assert.assertEquals(maxRestaurants, res.getResult().size());
    }

    @Test
    public void searchBySpecialty() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final RestaurantSpecialty restaurantSpeciality = RestaurantConstants.RESTAURANTS_SPECIALITY.get(0);
        List<RestaurantSpecialty> specialty = Collections.singletonList(restaurantSpeciality);
        for(int i=0; i<maxRestaurants; i++){
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setSpecialty(restaurantSpeciality);
        }
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, null, specialty);
        Assert.assertEquals(maxRestaurants, res.getResult().size());
    }

    @Test
    public void searchBySpecialties() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<RestaurantSpecialty> specialties = RestaurantConstants.RESTAURANTS_SPECIALITY;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, null, specialties);
        Assert.assertEquals(maxRestaurants, res.getResult().size());
    }

    @Test
    public void searchBySingleTag() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<RestaurantTags> tag = Collections.singletonList(RestaurantTags.fromOrdinal(1));
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, tag, null);
        Assert.assertEquals(maxRestaurants - 1, res.getResult().size());
    }

    @Test
    public void searchByTags() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<RestaurantTags> tag = RestaurantConstants.RESTAURANTS_TAGS.get(0);
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, tag, null);
        Assert.assertEquals(maxRestaurants - 1, res.getResult().size());
    }

    @Test
    @Rollback
    public void searchByNameAndTag() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final String restaurantName = "restaurant";
        for(int i=0; i<maxRestaurants ; i++){
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }
        List<RestaurantTags> tag = Collections.singletonList(RestaurantTags.fromOrdinal(2));

        PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, tag, null);
        Assert.assertEquals(maxRestaurants-2, res.getResult().size());
        for(int i=0; i<res.getResult().size();i++){
            Assert.assertTrue(res.getResult().get(i).getRestaurantId() != RestaurantConstants.RESTAURANT_IDS[2]);
        }
    }

    @Test
    @Rollback
    public void searchByNameAndSpecialty() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final String restaurantName = "restaurant";
        for(int i=0; i<maxRestaurants ; i++){
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }
        List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0));

        PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, null, specialty);
        Assert.assertEquals(1, res.getResult().size());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(0).getRestaurantId());
    }

    @Test
    @Rollback
    public void searchByTagAndSpecialty() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0));
        List<RestaurantTags> tag = RestaurantConstants.RESTAURANTS_TAGS.get(0);

        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, tag, specialty);
        Assert.assertEquals(1, res.getResult().size());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(0).getRestaurantId());
    }

    @Test
    @Rollback
    public void searchByNameAndTagAndSpecialty() throws SQLException {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0));
        List<RestaurantTags> tag = RestaurantConstants.RESTAURANTS_TAGS.get(0);
        final String restaurantName = "restaurant";
        for(int i=0; i<maxRestaurants ; i++){
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }

        PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, tag, specialty);
        Assert.assertEquals(1, res.getResult().size());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0].longValue(), res.getResult().get(0).getRestaurantId());
    }
}
