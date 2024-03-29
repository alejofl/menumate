package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.ProductConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.ReviewConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
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
import java.util.*;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class RestaurantJpaDaoTest {
    private static final long NON_EXISTENT_RESTAURANT_ID = 5000;
    private static final String NON_EXISTENT_RESTAURANT_NAME = "Nonexistent Restaurant";
    private static final String NON_EXISTENT_RESTAURANT_EMAIL = "nonexistent@restaurant.com";
    private static final double PRICE_ACCEPTABLE_DELTA = 0.005;

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
    public void testFindById() {
        final Optional<Restaurant> maybeRestaurant = restaurantDao.getById(RestaurantConstants.RESTAURANT_IDS[0]);

        assertTrue(maybeRestaurant.isPresent());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], maybeRestaurant.get().getRestaurantId().longValue());
        assertEquals(UserConstants.RESTAURANT_OWNER_ID, maybeRestaurant.get().getOwnerUserId());
        assertEquals(RestaurantConstants.RESTAURANT_NAMES[0], maybeRestaurant.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_EMAIL, maybeRestaurant.get().getEmail());
        assertEquals(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0).ordinal(), maybeRestaurant.get().getSpecialty().ordinal());
        assertEquals(RestaurantConstants.RESTAURANT_ADDRESS, maybeRestaurant.get().getAddress());
        assertEquals(RestaurantConstants.RESTAURANT_DESCRIPTION, maybeRestaurant.get().getDescription());
        assertEquals(RestaurantConstants.MAX_TABLES, maybeRestaurant.get().getMaxTables());
        assertEquals(RestaurantConstants.RESTAURANT_IS_ACTIVE, maybeRestaurant.get().getIsActive());
        assertEquals(RestaurantConstants.RESTAURANT_DELETED, maybeRestaurant.get().getDeleted());
        assertEquals(RestaurantConstants.RESTAURANT_CREATION_DATE.getDayOfYear(), maybeRestaurant.get().getDateCreated().getDayOfYear());
    }

    @Test
    @Rollback
    public void testGetByIdInactiveRestaurant() {
        final Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[0]);
        restaurant.setIsActive(false);
        em.flush();

        final Optional<Restaurant> maybeRestaurant = restaurantDao.getById(RestaurantConstants.RESTAURANT_IDS[0]);
        assertTrue(maybeRestaurant.isPresent());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], maybeRestaurant.get().getRestaurantId().longValue());
        assertEquals(UserConstants.RESTAURANT_OWNER_ID, maybeRestaurant.get().getOwnerUserId());
        assertEquals(RestaurantConstants.RESTAURANT_NAMES[0], maybeRestaurant.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_EMAIL, maybeRestaurant.get().getEmail());
        assertEquals(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0).ordinal(), maybeRestaurant.get().getSpecialty().ordinal());
        assertEquals(RestaurantConstants.RESTAURANT_ADDRESS, maybeRestaurant.get().getAddress());
        assertEquals(RestaurantConstants.RESTAURANT_DESCRIPTION, maybeRestaurant.get().getDescription());
        assertEquals(RestaurantConstants.MAX_TABLES, maybeRestaurant.get().getMaxTables());
        assertEquals(!RestaurantConstants.RESTAURANT_IS_ACTIVE, maybeRestaurant.get().getIsActive());
        assertEquals(RestaurantConstants.RESTAURANT_DELETED, maybeRestaurant.get().getDeleted());
        assertEquals(RestaurantConstants.RESTAURANT_CREATION_DATE.getDayOfYear(), maybeRestaurant.get().getDateCreated().getDayOfYear());
    }

    @Test
    @Rollback
    public void testGetByIdDeletedRestaurant() {
        final Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[0]);
        restaurant.setIsActive(false);
        restaurant.setDeleted(true);
        em.flush();

        final Optional<Restaurant> maybeRestaurant = restaurantDao.getById(RestaurantConstants.RESTAURANT_IDS[0]);
        assertTrue(maybeRestaurant.isPresent());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], maybeRestaurant.get().getRestaurantId().longValue());
        assertEquals(UserConstants.RESTAURANT_OWNER_ID, maybeRestaurant.get().getOwnerUserId());
        assertEquals(RestaurantConstants.RESTAURANT_NAMES[0], maybeRestaurant.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_EMAIL, maybeRestaurant.get().getEmail());
        assertEquals(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0).ordinal(), maybeRestaurant.get().getSpecialty().ordinal());
        assertEquals(RestaurantConstants.RESTAURANT_ADDRESS, maybeRestaurant.get().getAddress());
        assertEquals(RestaurantConstants.RESTAURANT_DESCRIPTION, maybeRestaurant.get().getDescription());
        assertEquals(RestaurantConstants.MAX_TABLES, maybeRestaurant.get().getMaxTables());
        assertEquals(!RestaurantConstants.RESTAURANT_IS_ACTIVE, maybeRestaurant.get().getIsActive());
        assertEquals(!RestaurantConstants.RESTAURANT_DELETED, maybeRestaurant.get().getDeleted());
        assertEquals(RestaurantConstants.RESTAURANT_CREATION_DATE.getDayOfYear(), maybeRestaurant.get().getDateCreated().getDayOfYear());
    }

    @Test
    public void testFindByIdNotFound() {
        final Optional<Restaurant> maybeRestaurant = restaurantDao.getById(NON_EXISTENT_RESTAURANT_ID);

        assertFalse(maybeRestaurant.isPresent());
    }

    @Test
    @Rollback
    public void testCreation() {
        final Restaurant restaurant = restaurantDao.create(
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

        assertNotNull(restaurant);
        assertEquals(NON_EXISTENT_RESTAURANT_NAME, restaurant.getName());
        assertEquals(NON_EXISTENT_RESTAURANT_EMAIL, restaurant.getEmail());
        assertEquals(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0).ordinal(), restaurant.getSpecialty().ordinal());
        assertEquals(UserConstants.RESTAURANT_OWNER_ID, restaurant.getOwnerUserId());
        assertEquals(RestaurantConstants.RESTAURANT_ADDRESS, restaurant.getAddress());
        assertEquals(RestaurantConstants.RESTAURANT_DESCRIPTION, restaurant.getDescription());
        assertEquals(RestaurantConstants.MAX_TABLES, restaurant.getMaxTables());
        assertEquals(RestaurantConstants.RESTAURANT_IS_ACTIVE, restaurant.getIsActive());
        assertEquals(RestaurantConstants.RESTAURANT_DELETED, restaurant.getDeleted());
    }

    @Test
    @Rollback
    public void testDeletion() {
        restaurantDao.delete(RestaurantConstants.RESTAURANT_IDS[0]);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurants", "deleted = true"));
    }

    @Test
    public void searchEmpty() {
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(null, 1, RestaurantConstants.RESTAURANT_IDS.length, null, false, null, null);
        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, res.getResult().size());
    }

    @Test
    public void searchSortedByNameAsc() {
        final PaginatedResult<RestaurantDetails> restaurantsDetails = restaurantDao.search(
                "", 1,
                RestaurantConstants.RESTAURANT_IDS.length,
                RestaurantOrderBy.ALPHABETIC,
                false, null, null
        );

        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, restaurantsDetails.getTotalCount());
        int j = 0;
        for (RestaurantDetails rd : restaurantsDetails.getResult()) {
            assertEquals(Character.toString((char) ('A' + j)), rd.getRestaurant().getName());
            j++;
        }
    }

    @Test
    public void searchSortedByNameDesc() {
        final PaginatedResult<RestaurantDetails> restaurantsDetails = restaurantDao.search(
                "", 1,
                RestaurantConstants.RESTAURANT_IDS.length,
                RestaurantOrderBy.ALPHABETIC,
                true, null, null
        );
        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, restaurantsDetails.getTotalCount());

        int j = RestaurantConstants.RESTAURANT_IDS.length - 1;
        for (RestaurantDetails rd : restaurantsDetails.getResult()) {
            assertEquals(Character.toString((char) ('A' + j)), rd.getRestaurant().getName());
            j--;
        }
    }

    @Test
    public void searchSortedByRatingAsc() {
        final List<Double> ratingAvg = ReviewConstants.AVERAGE_LIST;
        ratingAvg.sort(Comparator.naturalOrder());

        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(
                "", 1, maxRestaurants,
                RestaurantOrderBy.RATING,
                false, null, null
        );

        int size = res.getResult().size();
        assertEquals(maxRestaurants, res.getPageSize());
        assertEquals(maxRestaurants, res.getTotalCount());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[3], res.getResult().get(0).getRestaurantId());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(maxRestaurants - 1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            assertEquals(ratingAvg.get(i), res.getResult().get(i).getAverageRating(), PRICE_ACCEPTABLE_DELTA);
        }
    }

    @Test
    public void searchSortedByRatingDesc() {
        final List<Double> ratingAvg = ReviewConstants.AVERAGE_LIST;
        ratingAvg.sort(Comparator.reverseOrder());

        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(
                "", 1, maxRestaurants,
                RestaurantOrderBy.RATING,
                true, null, null
        );

        int size = res.getResult().size();
        assertEquals(maxRestaurants, res.getPageSize());
        assertEquals(maxRestaurants, res.getTotalCount());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(0).getRestaurantId());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[3], res.getResult().get(maxRestaurants - 1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            assertEquals(ratingAvg.get(i), res.getResult().get(i).getAverageRating(), PRICE_ACCEPTABLE_DELTA);
        }
    }

    @Test
    public void searchSortedByPriceAverageDesc() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<Double> avgPrice = ProductConstants.AVERAGE_LIST;
        avgPrice.sort(Comparator.reverseOrder());

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.PRICE, true, null, null);

        int size = res.getResult().size();
        assertEquals(maxRestaurants, res.getPageSize());
        assertEquals(maxRestaurants, res.getTotalCount());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[1], res.getResult().get(0).getRestaurantId());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[2], res.getResult().get(maxRestaurants - 1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            assertEquals(avgPrice.get(i), res.getResult().get(i).getAverageProductPrice(), 0.1);
        }
    }

    @Test
    public void searchSortedByPriceAverageAsc() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<Double> avgPrice = ProductConstants.AVERAGE_LIST;
        avgPrice.sort(Comparator.naturalOrder());

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.PRICE, false, null, null);

        int size = res.getResult().size();
        assertEquals(maxRestaurants, res.getPageSize());
        assertEquals(maxRestaurants, res.getTotalCount());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[2], res.getResult().get(0).getRestaurantId());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[1], res.getResult().get(maxRestaurants - 1).getRestaurantId());
        for (int i = 0; i < size; i++) {
            assertEquals(avgPrice.get(i), res.getResult().get(i).getAverageProductPrice(), 0.1);
        }
    }

    @Test
    public void searchSortedByCreationDateAsc() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RestaurantConstants.RESTAURANT_IDS.length, RestaurantOrderBy.DATE, false, null, null);
        assertEquals(maxRestaurants, res.getResult().size());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[maxRestaurants - 1], res.getResult().get(0).getRestaurantId());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(maxRestaurants - 1).getRestaurantId());
    }

    @Test
    public void getSortedByCreationDateDesc() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.DATE, true, null, null);
        assertEquals(maxRestaurants, res.getResult().size());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(0).getRestaurantId());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[maxRestaurants - 1], res.getResult().get(maxRestaurants - 1).getRestaurantId());
    }

    @Test
    @Rollback
    public void searchByRestaurantName() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final String restaurantName = RestaurantConstants.RESTAURANT_NAMES[0];
        for (int i = 0; i < maxRestaurants; i++) {
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }
        em.flush();

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, null, null);

        // Assert all restaurants are present
        assertEquals(maxRestaurants, res.getResult().size());
        assertFalse(Arrays.stream(RestaurantConstants.RESTAURANT_IDS).anyMatch(
                id -> res.getResult().stream().noneMatch(rd -> rd.getRestaurantId() == id)
        ));
    }

    @Test
    public void searchBySpecialty() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final RestaurantSpecialty restaurantSpeciality = RestaurantConstants.RESTAURANTS_SPECIALITY.get(0);
        final List<RestaurantSpecialty> specialty = Collections.singletonList(restaurantSpeciality);
        for (int i = 0; i < maxRestaurants; i++) {
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setSpecialty(restaurantSpeciality);
        }
        em.flush();

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, null, specialty);
        assertEquals(maxRestaurants, res.getResult().size());
        assertFalse(Arrays.stream(RestaurantConstants.RESTAURANT_IDS).anyMatch(
                id -> res.getResult().stream().noneMatch(rd -> rd.getRestaurantId() == id)
        ));
    }

    @Test
    public void searchBySpecialties() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<RestaurantSpecialty> specialties = RestaurantConstants.RESTAURANTS_SPECIALITY;
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, null, specialties);
        assertEquals(maxRestaurants, res.getResult().size());
        assertFalse(Arrays.stream(RestaurantConstants.RESTAURANT_IDS).anyMatch(
                id -> res.getResult().stream().noneMatch(rd -> rd.getRestaurantId() == id)
        ));
    }

    @Test
    public void searchBySingleTag() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<RestaurantTags> tag = Collections.singletonList(RestaurantTags.fromOrdinal(1));
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, tag, null);
        assertEquals(maxRestaurants - 1, res.getResult().size());
        assertFalse(Arrays.stream(RestaurantConstants.RESTAURANT_IDS).limit(maxRestaurants - 1).anyMatch(
                id -> res.getResult().stream().noneMatch(rd -> rd.getRestaurantId() == id)
        ));
    }

    @Test
    public void searchByTags() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<RestaurantTags> tag = RestaurantConstants.RESTAURANTS_TAGS.get(0);
        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, tag, null);
        assertEquals(maxRestaurants - 1, res.getResult().size());
        assertFalse(Arrays.stream(RestaurantConstants.RESTAURANT_IDS).limit(maxRestaurants - 1).anyMatch(
                id -> res.getResult().stream().noneMatch(rd -> rd.getRestaurantId() == id)
        ));
    }

    @Test
    @Rollback
    public void searchByNameAndTag() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final String restaurantName = "restaurant";
        for (int i = 0; i < maxRestaurants; i++) {
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }
        final List<RestaurantTags> tag = Collections.singletonList(RestaurantTags.fromOrdinal(2));
        em.flush();

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, tag, null);

        assertEquals(maxRestaurants - 2, res.getResult().size());
        assertFalse(Arrays.stream(RestaurantConstants.RESTAURANT_IDS).limit(2).anyMatch(
                id -> res.getResult().stream().noneMatch(rd -> rd.getRestaurantId() == id)
        ));

        for (int i = 0; i < res.getResult().size(); i++) {
            assertTrue(res.getResult().get(i).getRestaurantId() != RestaurantConstants.RESTAURANT_IDS[2]);
        }
    }

    @Test
    @Rollback
    public void searchByNameAndSpecialty() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final String restaurantName = "restaurant";
        for (int i = 0; i < maxRestaurants; i++) {
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }
        final List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0));
        em.flush();

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, null, specialty);
        assertEquals(1, res.getResult().size());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(0).getRestaurantId());
    }

    @Test
    @Rollback
    public void searchByTagAndSpecialty() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0));
        final List<RestaurantTags> tag = RestaurantConstants.RESTAURANTS_TAGS.get(0);

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, null, false, tag, specialty);
        assertEquals(1, res.getResult().size());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(0).getRestaurantId());
    }

    @Test
    @Rollback
    public void searchByNameAndTagAndSpecialty() {
        final int maxRestaurants = RestaurantConstants.RESTAURANT_IDS.length;
        final List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantConstants.RESTAURANTS_SPECIALITY.get(0));
        final List<RestaurantTags> tag = RestaurantConstants.RESTAURANTS_TAGS.get(0);
        final String restaurantName = "restaurant";
        for (int i = 0; i < maxRestaurants; i++) {
            Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[i]);
            restaurant.setName(restaurantName);
        }

        final PaginatedResult<RestaurantDetails> res = restaurantDao.search(restaurantName, 1, maxRestaurants, null, false, tag, specialty);
        assertEquals(1, res.getResult().size());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], res.getResult().get(0).getRestaurantId());
    }

    @Test
    public void getRestaurantDetails() {
        long restaurantId = RestaurantConstants.RESTAURANT_IDS[0];
        final Optional<RestaurantDetails> maybeRestaurantDetails = restaurantDao.getRestaurantDetails(restaurantId);

        assertTrue(maybeRestaurantDetails.isPresent());
        assertEquals(restaurantId, maybeRestaurantDetails.get().getRestaurantId());
    }

    @Test
    public void getNoRestaurantDetails() {
        final Optional<RestaurantDetails> maybeRestaurantDetails = restaurantDao.getRestaurantDetails(RestaurantConstants.RESTAURANT_ID_NON_EXISTENT);
        assertFalse(maybeRestaurantDetails.isPresent());
    }

    @Test
    @Rollback
    public void getInactiveRestaurantDetails() {
        final Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[0]);
        restaurant.setIsActive(false);
        em.flush();
        final Optional<RestaurantDetails> maybeRestaurantDetails = restaurantDao.getRestaurantDetails(restaurant.getRestaurantId());
        assertTrue(maybeRestaurantDetails.isPresent());
    }

    @Test
    @Rollback
    public void getDeletedRestaurantDetails() {
        final Restaurant restaurant = em.find(Restaurant.class, RestaurantConstants.RESTAURANT_IDS[0]);
        restaurant.setIsActive(false);
        restaurant.setDeleted(true);
        em.flush();
        final Optional<RestaurantDetails> maybeRestaurantDetails = restaurantDao.getRestaurantDetails(restaurant.getRestaurantId());
        assertTrue(maybeRestaurantDetails.isPresent());
    }
}
