package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RestaurantJdbcDaoTest {

    private static final int ID = 5123;
    private static final int MAX_TABLES = 20;
    private static final String NAME = "pedros";
    private static final String EMAIL = "pedros@frompedros.com";
    private static final int USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";

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
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + USER_ID + ", " + MAX_TABLES + ")");

        Optional<Restaurant> maybeRestaurant = restaurantDao.getById(ID);

        Assert.assertTrue(maybeRestaurant.isPresent());
        Assert.assertEquals(ID, maybeRestaurant.get().getRestaurantId());
        Assert.assertEquals(NAME, maybeRestaurant.get().getName());
        Assert.assertEquals(EMAIL, maybeRestaurant.get().getEmail());
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
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "', " + USER_ID + ", " + MAX_TABLES + ")");
        Assert.assertTrue(restaurantDao.delete(ID));
    }
}
