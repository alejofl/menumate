package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.model.util.Triplet;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RolesJdbcDaoTest {
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final long OWNER_ID = 313;
    private static final String OWNER_EMAIL = "pedro@pedro.com";
    private static final String OWNER_PASSWORD = "mega12secreto34";
    private static final String OWNER_NAME = "Pedro Parker";
    private static final long RESTAURANT_ID1 = 5123;
    private static final String RESTAURANT_NAME1 = "pedros";
    private static final String RESTAURANT_EMAIL1 = "pedros@frompedros.com";
    private static final long RESTAURANT_ID2 = 4242;
    private static final String RESTAURANT_NAME2 = "La Mejor Pizza";
    private static final int SPECIALTY = 1;
    private static final String RESTAURANT_EMAIL2 = "pizzeria@pizza.com";
    private static final int MAX_TABLES = 20;
    private static final RestaurantRoleLevel ROLE = RestaurantRoleLevel.MANAGER;
    private static final long USER_ID_NONE = 1234;
    private static final long RESTAURANT_ID_NONE = 1234;

    @Autowired
    private DataSource ds;

    @Autowired
    private RolesJdbcDao rolesDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "restaurants", "restaurant_roles");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + OWNER_ID + ", '" + OWNER_EMAIL + "', '" + OWNER_PASSWORD + "', '" + OWNER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + RESTAURANT_ID1 + ", '" + RESTAURANT_NAME1 + "', '" + RESTAURANT_EMAIL1 + "', " + SPECIALTY + ", " + OWNER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, owner_user_id, max_tables) VALUES (" + RESTAURANT_ID2 + ", '" + RESTAURANT_NAME2 + "', '" + RESTAURANT_EMAIL2 + "', " + SPECIALTY + ", " + OWNER_ID + ", " + MAX_TABLES + ")");
    }

    @Test
    public void testGetNoRoleOnNonexistingUserNonexistingRestaurant() {
        Assert.assertFalse(rolesDao.getRole(USER_ID_NONE, RESTAURANT_ID_NONE).isPresent());
    }

    @Test
    public void testGetNoRoleOnNonexistingUser() {
        Assert.assertFalse(rolesDao.getRole(USER_ID_NONE, RESTAURANT_ID1).isPresent());
        Assert.assertFalse(rolesDao.getRole(USER_ID_NONE, RESTAURANT_ID2).isPresent());
    }

    @Test
    public void testGetNoRoleOnNonexistingRestaurant() {
        Assert.assertFalse(rolesDao.getRole(USER_ID, RESTAURANT_ID_NONE).isPresent());
        Assert.assertFalse(rolesDao.getRole(OWNER_ID, RESTAURANT_ID_NONE).isPresent());
    }

    @Test
    public void testGetNoRole() {
        Assert.assertFalse(rolesDao.getRole(USER_ID, RESTAURANT_ID1).isPresent());
        Assert.assertFalse(rolesDao.getRole(USER_ID, RESTAURANT_ID2).isPresent());
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

    @Test
    public void testHasRoleWhenOwner() {
        Assert.assertTrue(rolesDao.doesUserHaveRole(OWNER_ID, RESTAURANT_ID1, RestaurantRoleLevel.OWNER));
        Assert.assertTrue(rolesDao.doesUserHaveRole(OWNER_ID, RESTAURANT_ID1, RestaurantRoleLevel.ADMIN));
        Assert.assertTrue(rolesDao.doesUserHaveRole(OWNER_ID, RESTAURANT_ID1, RestaurantRoleLevel.MANAGER));
        Assert.assertTrue(rolesDao.doesUserHaveRole(OWNER_ID, RESTAURANT_ID1, RestaurantRoleLevel.ORDER_HANDLER));
    }

    @Test
    public void testHasRoleWhenNothing() {
        Assert.assertFalse(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.OWNER));
        Assert.assertFalse(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.ADMIN));
        Assert.assertFalse(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.MANAGER));
        Assert.assertFalse(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.ORDER_HANDLER));
    }

    @Test
    public void testHasRoleWhenManager() {
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID1 + ", " + RestaurantRoleLevel.MANAGER.ordinal() + ")");

        Assert.assertFalse(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.OWNER));
        Assert.assertFalse(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.ADMIN));
        Assert.assertTrue(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.MANAGER));
        Assert.assertTrue(rolesDao.doesUserHaveRole(USER_ID, RESTAURANT_ID1, RestaurantRoleLevel.ORDER_HANDLER));
    }

    @Test
    public void testGetByRestaurantNone() {
        List<Pair<User, RestaurantRoleLevel>> result = rolesDao.getByRestaurant(RESTAURANT_ID_NONE);

        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetByRestaurantOnlyOwner() {
        List<Pair<User, RestaurantRoleLevel>> result = rolesDao.getByRestaurant(RESTAURANT_ID1);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(RestaurantRoleLevel.OWNER, result.get(0).getValue());
        Assert.assertEquals(OWNER_ID, result.get(0).getKey().getUserId());
    }

    @Test
    public void testGetByUserNone() {
        List<Triplet<Restaurant, RestaurantRoleLevel, Integer>> result = rolesDao.getByUser(USER_ID);

        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetByUserWhenOwner() {
        List<Triplet<Restaurant, RestaurantRoleLevel, Integer>> result = rolesDao.getByUser(OWNER_ID);

        Assert.assertEquals(2, result.size());

        long[] restaurantsExpected = new long[] {RESTAURANT_ID1, RESTAURANT_ID2};
        long[] restaurantsGot = new long[] {result.get(0).getX().getRestaurantId(), result.get(1).getX().getRestaurantId()};
        Arrays.sort(restaurantsGot);
        Arrays.sort(restaurantsExpected);
        Assert.assertArrayEquals(restaurantsExpected, restaurantsGot);

        Assert.assertEquals(RestaurantRoleLevel.OWNER, result.get(0).getY());
        Assert.assertEquals(RestaurantRoleLevel.OWNER, result.get(1).getY());
        Assert.assertEquals(0, result.get(0).getZ().intValue());
        Assert.assertEquals(0, result.get(1).getZ().intValue());
    }
}
