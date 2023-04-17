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
    private static final String NAME = "pedros";
    private static final String EMAIL = "pedros@frompedros.com";

    @Autowired
    private DataSource ds;

    @Autowired
    private RestaurantJdbcDao restaurantDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "restaurants");
    }

    @Test
    public void testFindById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "')");

        Optional<Restaurant> maybeRestaurant = restaurantDao.getById(ID);

        Assert.assertTrue(maybeRestaurant.isPresent());
        Assert.assertEquals(ID, maybeRestaurant.get().getRestaurantId());
        Assert.assertEquals(NAME, maybeRestaurant.get().getName());
        Assert.assertEquals(EMAIL, maybeRestaurant.get().getEmail());
    }

    @Test
    public void testCreation() throws SQLException {
        Restaurant restaurant = restaurantDao.create(NAME, EMAIL);

        Assert.assertNotNull(restaurant);
        Assert.assertEquals(NAME, restaurant.getName());
        Assert.assertEquals(EMAIL, restaurant.getEmail());
    }

    @Test
    public void testDeletion() throws SQLException {
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email) VALUES (" + ID + ", '" + NAME + "', '" + EMAIL + "')");
        Assert.assertTrue(restaurantDao.delete(ID));
    }
}
