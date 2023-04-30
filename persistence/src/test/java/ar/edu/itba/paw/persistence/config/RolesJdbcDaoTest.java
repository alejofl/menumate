package ar.edu.itba.paw.persistence.config;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.RolesDao;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RolesJdbcDaoTest {
    private static final int USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final int OWNER_ID = 313;
    private static final String OWNER_EMAIL = "pedro@pedro.com";
    private static final String OWNER_PASSWORD = "mega12secreto34";
    private static final String OWNER_NAME = "Pedro Parker";
    private static final int RESTAURANT_ID1 = 5123;
    private static final String RESTAURANT_NAME1 = "pedros";
    private static final String RESTAURANT_EMAIL1 = "pedros@frompedros.com";
    private static final int RESTAURANT_ID2 = 4242;
    private static final String RESTAURANT_NAME2 = "La Mejor Pizza";
    private static final String RESTAURANT_EMAIL2 = "pizzeria@pizza.com";
    private static final RestaurantRoleLevel ROLE = RestaurantRoleLevel.MANAGER;

    @Autowired
    private DataSource ds;

    @Autowired
    private RolesDao rolesDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "restaurants", "restaurant_roles");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + OWNER_ID + ", '" + OWNER_EMAIL + "', '" + OWNER_PASSWORD + "', '" + OWNER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id) VALUES (" + RESTAURANT_ID1 + ", '" + RESTAURANT_NAME1 + "', '" + RESTAURANT_EMAIL1 + "', " + OWNER_ID + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id) VALUES (" + RESTAURANT_ID2 + ", '" + RESTAURANT_NAME2 + "', '" + RESTAURANT_EMAIL2 + "', " + OWNER_ID + ")");
    }

    @Test
    public void testGetRoleWhenNone() {
        Assert.assertFalse(rolesDao.getRole(USER_ID, RESTAURANT_ID1).isPresent());
    }

    @Test
    public void testGetRoleWhenOwner() {
        Optional<RestaurantRoleLevel> role = rolesDao.getRole(OWNER_ID, RESTAURANT_ID1);

        Assert.assertTrue(role.isPresent());
        Assert.assertEquals(RestaurantRoleLevel.OWNER, role.get());
    }

    @Test
    public void testGetRoleWhenNotOwner() {
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID1 + ", " + ROLE.ordinal() + ")");

        Optional<RestaurantRoleLevel> role = rolesDao.getRole(USER_ID, RESTAURANT_ID1);

        Assert.assertTrue(role.isPresent());
        Assert.assertEquals(ROLE, role.get());
    }
}