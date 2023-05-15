package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantTags;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RestaurantJdbcDaoTest {

    private static final long ID = 5123;
    private static final int MAX_TABLES = 20;
    private static final String NAME = "pedros";
    private static final int SPECIALTY = 2;
    private static final String EMAIL = "pedros@frompedros.com";
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final RestaurantTags[] TAGS = RestaurantTags.values();
    private static final List<String> EXPECTED_NAMES = Arrays.asList("B", "D", "E", "A", "C", "F");
    private static final int PAGE_SIZE = 2;
    private static final int MIN_REVIEW_SCORE = 1;
    private static final int MAX_REVIEW_SCORE = 5;
    private static final int RESTAURANTS_QTY = 3;

    @Autowired
    private DataSource ds;

    @Autowired
    private RestaurantJdbcDao restaurantDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "restaurants", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
    }

    @Test
    public void testFindById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");

        Optional<Restaurant> maybeRestaurant = restaurantDao.getById(ID);

        Assert.assertTrue(maybeRestaurant.isPresent());
        Assert.assertEquals(ID, maybeRestaurant.get().getRestaurantId());
        Assert.assertEquals(NAME, maybeRestaurant.get().getName());
        Assert.assertEquals(EMAIL, maybeRestaurant.get().getEmail());
        Assert.assertEquals(SPECIALTY, maybeRestaurant.get().getSpecialty().ordinal());
    }

    /* FIXME Update test
    @Test
    public void testCreation() throws SQLException {
        Restaurant restaurant = restaurantDao.create(USER_ID, NAME, EMAIL);

        Assert.assertNotNull(restaurant);
        Assert.assertEquals(NAME, restaurant.getName());
        Assert.assertEquals(EMAIL, restaurant.getEmail());
    }*/

    @Test
    public void testDeletion() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        Assert.assertTrue(restaurantDao.delete(ID));
    }

    @Test
    public void addTagsToRestaurant() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        for (RestaurantTags tag : TAGS) {
            Assert.assertTrue(restaurantDao.addTag(ID, tag.ordinal()));
        }

        int count = jdbcTemplate.query("SELECT COUNT(*) AS c FROM restaurant_tags WHERE restaurant_id = ?",
                (rs, rowNum) -> rs.getInt("c"), ID).get(0);

        Assert.assertEquals(TAGS.length, count);
    }

    @Test
    public void getRestaurantTags() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        List<RestaurantTags> restaurantTags = restaurantDao.getTags(ID);
        Assert.assertTrue(restaurantTags.isEmpty());

        for (RestaurantTags tag : TAGS) {
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID + ", " + tag.ordinal() + ")");
        }

        restaurantTags = restaurantDao.getTags(ID);
        Assert.assertEquals(TAGS.length, restaurantTags.size());
    }

    @Test
    public void removeRestaurantTags() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        for (RestaurantTags tag : TAGS) {
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID + ", " + tag.ordinal() + ")");
        }

        int count;
        for (int i = 1; i < TAGS.length + 1; i++) {
            Assert.assertTrue(restaurantDao.removeTag(ID, TAGS[i - 1].ordinal()));
            count = jdbcTemplate.query("SELECT COUNT(*) AS c FROM restaurant_tags WHERE restaurant_id = ?",
                    (rs, rowNum) -> rs.getInt("c"), ID).get(0);
            Assert.assertEquals(count, TAGS.length - i);
        }
    }

//    @Test
//    public void getSortedByNameAsc() throws SQLException {
//
//        for (int i=0 ; i<EXPECTED_NAMES.size() ; i++) {
//            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + (i+1) + ", '" + EXPECTED_NAMES.get(i) + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
//        }
//
//        int j=0;
//        for(int i=0 ; i<(EXPECTED_NAMES.size()/PAGE_SIZE) ; i++) {
//            PaginatedResult<Restaurant> restaurants = restaurantDao.getSortedByName(i+1, PAGE_SIZE, "ASC");
//            Assert.assertEquals(EXPECTED_NAMES.size(), restaurants.getTotalCount());
//            for (int k=0 ; k<restaurants.getResult().size() ; k++) {
//                Assert.assertEquals((char) ('A' + j), restaurants.getResult().get(k).getName().charAt(0));
//                j++;
//            }
//        }
//    }
//
//    @Test
//    public void getSortedByNameDesc() throws SQLException {
//
//        for (int i=0 ; i<EXPECTED_NAMES.size() ; i++) {
//            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + (i+1) + ", '" + EXPECTED_NAMES.get(i) + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
//        }
//
//        int j=EXPECTED_NAMES.size()-1;
//        for(int i=0 ; i<(EXPECTED_NAMES.size()/PAGE_SIZE) ; i++) {
//            PaginatedResult<Restaurant> restaurants = restaurantDao.getSortedByName(i+1, PAGE_SIZE, "DESC");
//            Assert.assertEquals(EXPECTED_NAMES.size(), restaurants.getTotalCount());
//            for (int k=0 ; k<restaurants.getResult().size() ; k++) {
//                Assert.assertEquals((char) ('A' + j), restaurants.getResult().get(k).getName().charAt(0));
//                j--;
//            }
//        }
//    }
//
//    @Test
//    public void getSortedByPriceAverageAsc() throws SQLException {
//
//        List<Float> mockedReviewAverages = new ArrayList<>();
//        Random random;
//        ReviewJdbcDao reviewJdbcDao = mock(ReviewJdbcDao.class);
//        AverageCountPair averageCountPair = mock(AverageCountPair.class);
//
//        // Create restaurant
//        for(int i=1; i<RESTAURANTS_QTY+1; i++){
//            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
//        }
//
//        int sum = 0;
//        int randomScoreValue;
//        for(int i=0; i< RESTAURANTS_QTY*RESTAURANTS_QTY ; i++) {
//            random = new Random();
//            randomScoreValue = random.nextInt(MAX_REVIEW_SCORE - MIN_REVIEW_SCORE + 1) + MIN_REVIEW_SCORE;
//            sum+=randomScoreValue;
//            if(i % RESTAURANTS_QTY == 0){
//                mockedReviewAverages.add((float) (sum / RESTAURANTS_QTY));
//                sum = 0;
//            }
//        }
//        int maxIndex = mockedReviewAverages.indexOf(Collections.max(mockedReviewAverages));
//        int minIndex = mockedReviewAverages.indexOf(Collections.min(mockedReviewAverages));
//
//        for(int i=1; i<RESTAURANTS_QTY+1; i++){
//            when(reviewJdbcDao.getRestaurantAverage(i)).thenReturn(averageCountPair);
//            when(reviewJdbcDao.getRestaurantAverage(i).getAverage()).thenReturn(mockedReviewAverages.get(i-1));
//        }
//
//        PaginatedResult<Restaurant> res = restaurantDao.getSortedByPriceAverage(1, RESTAURANTS_QTY, "ASC");
//        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
//        Assert.assertEquals(minIndex + 1, res.getResult().get(minIndex).getRestaurantId());
//        Assert.assertEquals(maxIndex + 1, res.getResult().get(maxIndex).getRestaurantId());
//    }
//
//    @Test
//    public void getSortedByCreationDate() throws SQLException {
//
//        Random random = new Random();
//        List<LocalDateTime> mockedDates = new ArrayList<>();
//        LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);
//        LocalDateTime endDateTime = LocalDateTime.now();
//
//        for(int i=1; i<RESTAURANTS_QTY+1; i++){
//            LocalDateTime randomDateTime = startDateTime.plusSeconds(random.nextInt((int) Duration.between(startDateTime, endDateTime).getSeconds() + 1));
//            mockedDates.add(randomDateTime);
//            Timestamp aux = Timestamp.valueOf(mockedDates.get(i-1));
//            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables, date_created) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ", '" + aux + "')");
//        }
//
//        // Greatest
//        int maxIndex = mockedDates.indexOf(Collections.max(mockedDates));
//
//        // Oldest
//        int minIndex = mockedDates.indexOf(Collections.min(mockedDates));
//
//        PaginatedResult<Restaurant> res = restaurantDao.getSortedByCreationDate(1, RESTAURANTS_QTY, "ASC");
//        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
//        Assert.assertEquals(minIndex + 1, res.getResult().get(0).getRestaurantId());
//        Assert.assertEquals(maxIndex + 1, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());
//    }
//
//    @Test
//    public void getSortedByAveragePrice() throws SQLException {
//        //productJdbcDao.getRestaurantAveragePrice(r.getRestaurantId())
//        ProductJdbcDao productJdbcDao = mock(ProductJdbcDao.class);
//
//        for(int i=1; i<RESTAURANTS_QTY+1; i++){
//            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
//        }
//
//        List<Double> mockedPrices = new ArrayList<>(Collections.nCopies(RESTAURANTS_QTY, 10.0));
//        for(int i=1; i<RESTAURANTS_QTY+1; i++){
//            when(productJdbcDao.getRestaurantAveragePrice(i)).thenReturn(mockedPrices.get(i-1)*i);
//        }
//
//        PaginatedResult<Restaurant> res = restaurantDao.getSortedByAveragePrice(1, RESTAURANTS_QTY, "DESC");
//
//        int maxIndex = mockedPrices.indexOf(Collections.max(mockedPrices));
//        int minIndex = mockedPrices.indexOf(Collections.min(mockedPrices));
//
//        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
//        Assert.assertEquals(res.getResult().get(0).getRestaurantId(), maxIndex + 1);
//        Assert.assertEquals(res.getResult().get(0).getRestaurantId(), minIndex + 1);
//    }
}
