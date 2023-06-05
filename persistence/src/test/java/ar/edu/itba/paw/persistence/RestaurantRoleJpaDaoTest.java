package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.RestaurantRole;
import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class RestaurantRoleJpaDaoTest {

    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final boolean USER_IS_ACTIVE = true;
    private static final String USER_PREFERRED_LANGUAGE = "qx";
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
    private static final RestaurantRoleLevel ROLE1 = RestaurantRoleLevel.ADMIN;
    private static final RestaurantRoleLevel ROLE2 = RestaurantRoleLevel.ORDER_HANDLER;
    private static final long USER_ID_NONE = 1234;
    private static final long RESTAURANT_ID_NONE = 1234;
    private static final String RESTAURANT_ADDRESS1 = "Av. Siempreviva 742";
    private static final String RESTAURANT_ADDRESS2 = "Av. Corrientes 600";
    private static final String RESTAURANT_DESCRIPTION1 = "La mejor comida de la zona";
    private static final String RESTAURANT_DESCRIPTION2 = "La mejor pizza de capital";
    private static final boolean RESTAURANT_DELETED = false;
    private static final boolean RESTAURANT_IS_ACTIVE = true;


    @Autowired
    private DataSource ds;

    @Autowired
    private RestaurantRoleJpaDao rolesDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "restaurants", "restaurant_roles");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "', " + USER_IS_ACTIVE + ", '" + USER_PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + OWNER_ID + ", '" + OWNER_EMAIL + "', '" + OWNER_PASSWORD + "', '" + OWNER_NAME + "', " + USER_IS_ACTIVE + ", '" + USER_PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID1 + ", '" + RESTAURANT_NAME1 + "', '" + RESTAURANT_EMAIL1 + "', " + MAX_TABLES + ", " + SPECIALTY + ", " + OWNER_ID + ", '" + RESTAURANT_ADDRESS1 + "', '" + RESTAURANT_DESCRIPTION1 + "', '" + Timestamp.valueOf(LocalDateTime.now()) + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID2 + ", '" + RESTAURANT_NAME2 + "', '" + RESTAURANT_EMAIL2 + "', " + MAX_TABLES + ", " + SPECIALTY + ", " + OWNER_ID + ", '" + RESTAURANT_ADDRESS2 + "', '" + RESTAURANT_DESCRIPTION2 + "', '" + Timestamp.valueOf(LocalDateTime.now()) + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
    }

    @Test
    public void testCreate() {
        rolesDao.create(USER_ID, RESTAURANT_ID1, ROLE1);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id = " + USER_ID + " AND restaurant_id = " + RESTAURANT_ID1 + " AND role_level = " + ROLE1.ordinal()));
    }

    @Test
    public void testCreateExistingRole() {
        rolesDao.create(USER_ID, RESTAURANT_ID1, ROLE1);
        em.flush();
        Assert.assertThrows(EntityExistsException.class, () -> rolesDao.create(USER_ID, RESTAURANT_ID1, ROLE1));
    }

    @Test
    public void testDeleteThrowsWhenNonexistingRole() {
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID, RESTAURANT_ID1));
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID, RESTAURANT_ID2));
    }

    @Test
    public void testDeleteThrowsWhenNonexistingUser() {
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID_NONE, RESTAURANT_ID1));
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID_NONE, RESTAURANT_ID2));
    }

    @Test
    public void testDeleteWhenExisting() {
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID1 + ", " + ROLE1.ordinal() + ")");

        rolesDao.delete(USER_ID, RESTAURANT_ID1);
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id=" + USER_ID + " AND restaurant_id=" + RESTAURANT_ID1 + " AND role_level=" + ROLE2.ordinal()));
    }

    @Test
    public void testDeleteWhenNonexistingWithOtherRolePresent() {
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID2 + ", " + ROLE1.ordinal() + ")");
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID, RESTAURANT_ID1));
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id=" + USER_ID + " AND restaurant_id=" + RESTAURANT_ID2));
    }

    @Test
    public void testDeleteWhenExistingWithOtherRolePresent() {
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID1 + ", " + ROLE1.ordinal() + ")");
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID2 + ", " + ROLE1.ordinal() + ")");

        rolesDao.delete(USER_ID, RESTAURANT_ID1);
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id=" + USER_ID + " AND restaurant_id=" + RESTAURANT_ID1 + " AND role_level=" + ROLE2.ordinal()));
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id=" + USER_ID + " AND restaurant_id=" + RESTAURANT_ID2 + " AND role_level=" + ROLE1.ordinal()));
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
    public void testGetRoleWhenNotOwner() {
        jdbcTemplate.execute("INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (" + USER_ID + ", " + RESTAURANT_ID1 + ", " + ROLE1.ordinal() + ")");
        Optional<RestaurantRole> role = rolesDao.getRole(USER_ID, RESTAURANT_ID1);

        Assert.assertTrue(role.isPresent());
        Assert.assertEquals(ROLE1, role.get().getLevel());
    }

    @Test
    public void testGetByRestaurantNone() {
        List<RestaurantRole> result = rolesDao.getByRestaurant(RESTAURANT_ID_NONE);

        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetByUserNone() {
        List<RestaurantRoleDetails> result = rolesDao.getByUser(USER_ID);

        Assert.assertEquals(0, result.size());
    }
}
