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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RestaurantJdbcDaoTest {
/*
// TODO: Fix tests


    private static final long ID = 5123;
    private static final long ID2 = 2003;
    private static final long ID3 = 2004;
    private static final int MAX_TABLES = 20;
    private static final String NAME = "pedros";
    private static final String NAME2 = "pedros2";
    private static final Integer SPECIALTY = 2;
    private static final String EMAIL = "pedros@frompedros.com";
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final RestaurantTags[] TAGS = RestaurantTags.values();
    private static final List<String> EXPECTED_NAMES = Arrays.asList("B", "D", "E", "A", "C", "F");
    private static final int RESTAURANTS_QTY = 3;
    private static final int TAG_ID = 1;
    private static final String DESCRIPTION = "description";

    @Autowired
    private DataSource ds;

    @Autowired
    private RestaurantJpaDao restaurantDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "restaurants", "users", "orders", "order_reviews", "restaurant_tags");
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
        Assert.assertEquals(SPECIALTY.intValue(), maybeRestaurant.get().getSpecialty().ordinal());
    }

    @Test
    public void testCreation() throws SQLException {
        Restaurant restaurant = restaurantDao.create(USER_ID, NAME, EMAIL);

        Assert.assertNotNull(restaurant);
        Assert.assertEquals(NAME, restaurant.getName());
        Assert.assertEquals(EMAIL, restaurant.getEmail());
    }

    @Test
    public void testDeletion() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        restaurantDao.delete(ID);
    }

    @Test
    public void addTagsToRestaurant() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        for (RestaurantTags tag : TAGS) {
            restaurantDao.addTag(ID, tag.ordinal());
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
            restaurantDao.removeTag(ID, TAGS[i - 1].ordinal());
            count = jdbcTemplate.query("SELECT COUNT(*) AS c FROM restaurant_tags WHERE restaurant_id = ?",
                    (rs, rowNum) -> rs.getInt("c"), ID).get(0);
            Assert.assertEquals(count, TAGS.length - i);
        }
    }

    @Test
    public void searchSortedByNameAsc() throws SQLException {

        for (int i = 0; i < EXPECTED_NAMES.size(); i++) {
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + (i + 1) + ", '" + EXPECTED_NAMES.get(i) + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (i + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + (i + 1) + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (i + 1) + ", " + 5 + ", 'comment')");
        }

        int j = 0;
        final int maxRestaurants = EXPECTED_NAMES.size();
        PaginatedResult<RestaurantDetails> restaurantsDetails = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.ALPHABETIC, false, null, null);
        Assert.assertEquals(maxRestaurants, restaurantsDetails.getTotalCount());

        for (RestaurantDetails rd : restaurantsDetails.getResult()) {
            Assert.assertEquals(Character.toString((char) ('A' + j)), rd.getRestaurant().getName());
            j++;
        }
    }

    @Test
    public void searchSortedByNameDesc() throws SQLException {

        for (int i = 0; i < EXPECTED_NAMES.size(); i++) {
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + (i + 1) + ", '" + EXPECTED_NAMES.get(i) + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (i + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + (i + 1) + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (i + 1) + ", " + 5 + ", 'comment')");
        }

        int j = EXPECTED_NAMES.size() - 1;
        final int maxRestaurants = EXPECTED_NAMES.size();
        PaginatedResult<RestaurantDetails> restaurantsDetails = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.ALPHABETIC, true, null, null);
        Assert.assertEquals(maxRestaurants, restaurantsDetails.getTotalCount());

        for (RestaurantDetails rd : restaurantsDetails.getResult()) {
            Assert.assertEquals(Character.toString((char) ('A' + j)), rd.getRestaurant().getName());
            j--;
        }
    }

    @Test
    public void searchSortedByRatingAsc() throws SQLException {

        List<Double> ratingAvg = new ArrayList<>();
        final List<Integer> ratings1 = Arrays.asList(1, 1, 3, 5, 5, 5);
        final List<Integer> ratings2 = Arrays.asList(2, 2, 2, 2, 2, 2);
        final List<Integer> ratings3 = Arrays.asList(3, 3, 3, 3, 3, 3);
        ratingAvg.add(ratings1.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        ratingAvg.add(ratings2.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        ratingAvg.add(ratings3.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));

        for (int i = 1; i < RESTAURANTS_QTY+1; i++) {
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        }
        int j=0;
        for(int i=0; j<ratings1.size(); i++, j++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (j + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + 1 + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (j + 1) + ", " + ratings1.get(i) + ", 'comment')");
        }

        for(int i=0; i<ratings2.size(); i++, j++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (j + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + 2 + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (j + 1) + ", " + ratings2.get(i) + ", 'comment')");
        }

        for(int i=0; i<ratings3.size(); i++, j++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (j + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + 3 + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (j + 1) + ", " + ratings3.get(i) + ", 'comment')");
        }

        final int maxRestaurants = RESTAURANTS_QTY;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.RATING, false, null, null);

        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(RESTAURANTS_QTY, res.getTotalCount());
        Assert.assertEquals(2, res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(1, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());

        Collections.sort(ratingAvg);
        int size = res.getResult().size();
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(ratingAvg.get(i).intValue(),res.getResult().get(i).getAverageRating());
        }
    }

    @Test
    public void searchSortedByRatingDesc() throws SQLException {

        List<Double> ratingAvg = new ArrayList<>();
        final List<Integer> ratings1 = Arrays.asList(1, 1, 3, 5, 5, 5);
        final List<Integer> ratings2 = Arrays.asList(2, 2, 2, 2, 2, 2);
        final List<Integer> ratings3 = Arrays.asList(3, 3, 3, 3, 3, 3);
        ratingAvg.add(ratings1.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        ratingAvg.add(ratings2.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        ratingAvg.add(ratings3.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));

        for (int i = 1; i < RESTAURANTS_QTY+1; i++) {
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        }
        int j=0;
        for(int i=0; j<ratings1.size(); i++, j++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (j + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + 1 + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (j + 1) + ", " + ratings1.get(i) + ", 'comment')");
        }

        for(int i=0; i<ratings2.size(); i++, j++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (j + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + 2 + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (j + 1) + ", " + ratings2.get(i) + ", 'comment')");
        }

        for(int i=0; i<ratings3.size(); i++, j++){
            jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES (" + (j + 1) + ", " + OrderType.TAKEAWAY.ordinal() + ", " + 3 + ", " + USER_ID + ", now(), now(), now(), now())");
            jdbcTemplate.execute("INSERT INTO order_reviews (order_id, rating, comment) VALUES (" + (j + 1) + ", " + ratings3.get(i) + ", 'comment')");
        }

        final int maxRestaurants = RESTAURANTS_QTY;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.RATING, true, null, null);

        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(RESTAURANTS_QTY, res.getTotalCount());
        Assert.assertEquals(1, res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(2, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());

        ratingAvg.sort(Collections.reverseOrder());
        int size = res.getResult().size();
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(ratingAvg.get(i).intValue(),res.getResult().get(i).getAverageRating());
        }
    }

    @Test
    public void searchSortedByPriceAverageDesc() throws SQLException {

        for (int i = 1; i < RESTAURANTS_QTY+1; i++) {
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO categories (category_id, order_num, restaurant_id, name) VALUES (" + i + ", " + i + ", " + i + ", 'category')");
        }

        List<Double> avgPrice = new ArrayList<>();
        final List<Integer> prices1 = Arrays.asList(1, 1, 3, 5, 5, 5);
        final List<Integer> prices2 = Arrays.asList(2, 2, 2, 2, 2, 2);
        final List<Integer> prices3 = Arrays.asList(3, 3, 3, 3, 3, 3);
        avgPrice.add(prices1.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        avgPrice.add(prices2.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        avgPrice.add(prices3.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));

        int j=0;
        for(int i=0; i<prices1.size() ; i++, j++){
            jdbcTemplate.execute("INSERT INTO products (product_id, category_id, name, description, price) VALUES (" + (j + 1) + ", " + 1 + ", '" + NAME + "', '" + DESCRIPTION + "', " + prices1.get(i) + ")");
        }

        for(int i=0; i<prices2.size() ; i++, j++){
            jdbcTemplate.execute("INSERT INTO products (product_id, category_id, name, description, price) VALUES (" + (j + 1) + ", " + 2 + ", '" + NAME + "', '" + DESCRIPTION + "', " + prices2.get(i) + ")");
        }

        for(int i=0; i<prices3.size() ; i++, j++){
            jdbcTemplate.execute("INSERT INTO products (product_id, category_id, name, description, price) VALUES (" + (j + 1) + ", " + 3 + ", '" + NAME + "', '" + DESCRIPTION + "', " + prices3.get(i) + ")");
        }

        final int maxRestaurants = RESTAURANTS_QTY;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.PRICE, true, null, null);

        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(RESTAURANTS_QTY, res.getTotalCount());
        Assert.assertEquals(1, res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(2, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());

        avgPrice.sort(Collections.reverseOrder());
        int size = res.getResult().size();
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(avgPrice.get(i),res.getResult().get(i).getAverageProductPrice(), 0.1);
        }
    }

    @Test
    public void searchSortedByPriceAverageAsc() throws SQLException {

        for (int i = 1; i < RESTAURANTS_QTY+1; i++) {
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO categories (category_id, order_num, restaurant_id, name) VALUES (" + i + ", " + i + ", " + i + ", 'category')");
        }

        List<Double> avgPrice = new ArrayList<>();
        final List<Integer> prices1 = Arrays.asList(1, 1, 3, 5, 5, 5);
        final List<Integer> prices2 = Arrays.asList(2, 2, 2, 2, 2, 2);
        final List<Integer> prices3 = Arrays.asList(3, 3, 3, 3, 3, 3);
        avgPrice.add(prices1.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        avgPrice.add(prices2.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
        avgPrice.add(prices3.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));

        int j=0;
        for(int i=0; i<prices1.size() ; i++, j++){
            jdbcTemplate.execute("INSERT INTO products (product_id, category_id, name, description, price) VALUES (" + (j + 1) + ", " + 1 + ", '" + NAME + "', '" + DESCRIPTION + "', " + prices1.get(i) + ")");
        }

        for(int i=0; i<prices2.size() ; i++, j++){
            jdbcTemplate.execute("INSERT INTO products (product_id, category_id, name, description, price) VALUES (" + (j + 1) + ", " + 2 + ", '" + NAME + "', '" + DESCRIPTION + "', " + prices2.get(i) + ")");
        }

        for(int i=0; i<prices3.size() ; i++, j++){
            jdbcTemplate.execute("INSERT INTO products (product_id, category_id, name, description, price) VALUES (" + (j + 1) + ", " + 3 + ", '" + NAME + "', '" + DESCRIPTION + "', " + prices3.get(i) + ")");
        }

        final int maxRestaurants = RESTAURANTS_QTY;
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, maxRestaurants, RestaurantOrderBy.PRICE, false, null, null);

        Assert.assertEquals(maxRestaurants, res.getPageSize());
        Assert.assertEquals(RESTAURANTS_QTY, res.getTotalCount());
        Assert.assertEquals(2, res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(1, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());

        Collections.sort(avgPrice);
        int size = res.getResult().size();
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(avgPrice.get(i),res.getResult().get(i).getAverageProductPrice(), 0.1);
        }
    }

    @Test
    public void searchSortedByCreationDateAsc() throws SQLException {

        List<Timestamp> mockedDates = new ArrayList<>();
        mockedDates.add(Timestamp.valueOf("2019-01-01 00:00:00"));
        mockedDates.add(Timestamp.valueOf("2019-01-02 00:00:00"));
        mockedDates.add(Timestamp.valueOf("2019-01-03 00:00:00"));

        for(int i=1; i<RESTAURANTS_QTY+1 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables, date_created) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ", '" + mockedDates.get(i-1) + "')");
        }

        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RESTAURANTS_QTY, RestaurantOrderBy.DATE, false, null, null);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
        Assert.assertEquals(1, res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(3, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());
    }

    @Test
    public void getSortedByCreationDateDesc() throws SQLException {

        List<Timestamp> mockedDates = new ArrayList<>();
        mockedDates.add(Timestamp.valueOf("2019-01-01 00:00:00"));
        mockedDates.add(Timestamp.valueOf("2019-01-02 00:00:00"));
        mockedDates.add(Timestamp.valueOf("2019-01-03 00:00:00"));

        for(int i=1; i<RESTAURANTS_QTY+1 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables, date_created) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ", '" + mockedDates.get(i-1) + "')");
        }

        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RESTAURANTS_QTY, RestaurantOrderBy.DATE, true, null, null);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
        Assert.assertEquals(3, res.getResult().get(0).getRestaurantId());
        Assert.assertEquals(1, res.getResult().get(RESTAURANTS_QTY-1).getRestaurantId());
    }

    @Test
    public void searchByRestaurantName() throws SQLException {

        for(int i=1; i<4 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        }
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(NAME, 1, RESTAURANTS_QTY, null, false, null, null);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
    }

    @Test
    public void searchBySpecialty() throws SQLException {
        for(int i=1; i<RESTAURANTS_QTY+1 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        }
        List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantSpecialty.fromOrdinal(SPECIALTY));
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RESTAURANTS_QTY, null, false, null, specialty);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
    }

    @Test
    public void searchBySpecialties() throws SQLException {
        RestaurantSpecialty[] arr = RestaurantSpecialty.values();
        List<RestaurantSpecialty> specialties = new ArrayList<>();
        for(int i=1; i<RESTAURANTS_QTY+1 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + arr[i].ordinal() + ", " + USER_ID + ", " + MAX_TABLES + ")");
            specialties.add(RestaurantSpecialty.fromOrdinal(i));
        }
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RESTAURANTS_QTY, null, false, null, specialties);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
    }

    @Test
    public void searchBySingleTag() throws SQLException {

        int otherTagId = TAG_ID + 1;
        for(int i=1; i<RESTAURANTS_QTY+1 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + TAG_ID + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + otherTagId + ")");
        }
        List<RestaurantTags> tag = Collections.singletonList(RestaurantTags.fromOrdinal(TAG_ID));
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RESTAURANTS_QTY, null, false, tag, null);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
    }

    @Test
    public void searchByTags() throws SQLException {

        int otherTagId = TAG_ID + 1;
        for(int i=1; i<RESTAURANTS_QTY+1 ; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + TAG_ID + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + otherTagId + ")");
        }
        List<RestaurantTags> tag = Arrays.asList(RestaurantTags.fromOrdinal(TAG_ID), RestaurantTags.fromOrdinal(otherTagId));
        PaginatedResult<RestaurantDetails> res = restaurantDao.search("", 1, RESTAURANTS_QTY, null, false, tag, null);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
    }

    @Test
    public void searchByNameAndTag() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID2 + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID + ", " + TAG_ID + ")");
        List<RestaurantTags> tag = Collections.singletonList(RestaurantTags.fromOrdinal(TAG_ID));
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(NAME, 1, RESTAURANTS_QTY, null, false, tag, null);
        Assert.assertEquals(1, res.getResult().size());
        Assert.assertEquals(ID, res.getResult().get(0).getRestaurantId());
    }
    @Test
    public void searchByNameAndSpecialty() throws SQLException {
        int otherSpecialty = SPECIALTY + 1;
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + SPECIALTY + ", " + USER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID2 + ", '" + NAME + "', '" + EMAIL + "', " + otherSpecialty + ", " + USER_ID + ", " + MAX_TABLES + ")");
        List<RestaurantSpecialty> specialty = Collections.singletonList(RestaurantSpecialty.fromOrdinal(SPECIALTY));
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(NAME, 1, RESTAURANTS_QTY, null, false, null, specialty);
        Assert.assertEquals(1, res.getResult().size());
        Assert.assertEquals(ID, res.getResult().get(0).getRestaurantId());
    }
    @Test
    public void searchByTagAndSpecialty() throws SQLException {
        RestaurantSpecialty[] specialties = RestaurantSpecialty.values();
        RestaurantTags[] tags = RestaurantTags.values();
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + specialties[0].ordinal() + ", " + USER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID2 + ", '" + NAME + "', '" + EMAIL + "', " + specialties[0].ordinal() + ", " + USER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + ID3 + ", '" + NAME + "', '" + EMAIL + "', " + specialties[1].ordinal() + ", " + USER_ID + ", " + MAX_TABLES + ")");

        jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID + ", " + tags[0].ordinal() + ")");
        jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID + ", " + tags[1].ordinal() + ")");
        jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID2 + ", " + tags[1].ordinal() + ")");
        jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + ID3 + ", " + tags[0].ordinal() + ")");

        List<RestaurantSpecialty> specialty = Collections.singletonList(specialties[0]);
        List<RestaurantTags> tag = Arrays.asList(tags[0], tags[1]);
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(NAME, 1, RESTAURANTS_QTY, null, false, tag, specialty);
        Assert.assertEquals(2, res.getResult().size());
    }

    @Test
    public void searchByNameAndTagAndSpecialty() throws SQLException {
        RestaurantSpecialty[] specialties = RestaurantSpecialty.values();
        RestaurantTags[] tags = RestaurantTags.values();
        int i=0;
        for ( ; i<RESTAURANTS_QTY+1; i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME + "', '" + EMAIL + "', " + specialties[0].ordinal() + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + tags[0].ordinal() + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + tags[1].ordinal() + ")");
        }

        for( ; i<2*(RESTAURANTS_QTY+1); i++){
            jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + i + ", '" + NAME2 + "', '" + EMAIL + "', " + specialties[0].ordinal() + ", " + USER_ID + ", " + MAX_TABLES + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + tags[0].ordinal() + ")");
            jdbcTemplate.execute("INSERT INTO restaurant_tags (restaurant_id, tag_id) VALUES (" + i + ", " + tags[1].ordinal() + ")");
        }

        List<RestaurantSpecialty> specialty = Collections.singletonList(specialties[0]);
        List<RestaurantTags> tag = Arrays.asList(tags[0], tags[1]);
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(NAME, 1, RESTAURANTS_QTY, null, false, tag, specialty);
        Assert.assertEquals(RESTAURANTS_QTY, res.getResult().size());
        Assert.assertEquals(NAME, res.getResult().get(0).getRestaurant().getName());
    }

    @Test
    public void searchEmpty() throws SQLException {
        PaginatedResult<RestaurantDetails> res = restaurantDao.search(null, 1, RESTAURANTS_QTY, null, false, null, null);
        Assert.assertEquals(0, res.getResult().size());
    }
    */
}
