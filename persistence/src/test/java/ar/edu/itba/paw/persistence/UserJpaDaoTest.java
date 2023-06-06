package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.UserConstants;
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
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserJpaDaoTest {
    private static final long NON_EXISTENT_USER_ID = 5000;
    private static final String NON_EXISTENT_USER_EMAIL = "nonexistent@localhost";
    private static final String NON_EXISTENT_ADDRESS = "NON_EXISTENT_ADDRESS";
    private static final String NON_EXISTENT_NAME = "NON_EXISTENT_NAME";

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJpaDao userDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindActiveUserById() throws SQLException {
        Optional<User> maybeUser = userDao.getById(UserConstants.ACTIVE_USER_ID);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(UserConstants.ACTIVE_USER_ID, maybeUser.get().getUserId().longValue());
        Assert.assertEquals(UserConstants.ACTIVE_USER_EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(UserConstants.USERNAME, maybeUser.get().getName());
        Assert.assertTrue(maybeUser.get().getIsActive());
        Assert.assertEquals(UserConstants.PREFERRED_LANGUAGE, maybeUser.get().getPreferredLanguage());
    }

    @Test
    public void testFindNonActiveUserById() throws SQLException {
        Optional<User> maybeUser = userDao.getById(UserConstants.INACTIVE_USER_ID);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(UserConstants.INACTIVE_USER_ID, maybeUser.get().getUserId().longValue());
        Assert.assertEquals(UserConstants.INACTIVE_USER_EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(UserConstants.USERNAME, maybeUser.get().getName());
        Assert.assertFalse(maybeUser.get().getIsActive());
        Assert.assertEquals(UserConstants.PREFERRED_LANGUAGE, maybeUser.get().getPreferredLanguage());
    }

    @Test
    public void testFindActiveUserByEmail() throws SQLException {
        Optional<User> maybeUser = userDao.getByEmail(UserConstants.ACTIVE_USER_EMAIL);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(UserConstants.ACTIVE_USER_ID, maybeUser.get().getUserId().longValue());
        Assert.assertEquals(UserConstants.ACTIVE_USER_EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(UserConstants.USERNAME, maybeUser.get().getName());
        Assert.assertTrue(maybeUser.get().getIsActive());
        Assert.assertEquals(UserConstants.PREFERRED_LANGUAGE, maybeUser.get().getPreferredLanguage());
        Assert.assertEquals(LocalDateTime.now().getDayOfYear(), maybeUser.get().getDateJoined().getDayOfYear());
    }

    @Test
    public void testFindByIdDoesNotExist() throws SQLException {
        Optional<User> maybeUser = userDao.getById(NON_EXISTENT_USER_ID);
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindByEmailDoesNotExist() throws SQLException {
        Optional<User> maybeUser = userDao.getByEmail(NON_EXISTENT_USER_EMAIL);
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    @Rollback
    public void testCreate() {
        User user = userDao.create(NON_EXISTENT_USER_EMAIL, UserConstants.PASSWORD, UserConstants.USERNAME, UserConstants.PREFERRED_LANGUAGE);
        em.flush();

        Assert.assertNotNull(user);
        Assert.assertEquals(NON_EXISTENT_USER_EMAIL, user.getEmail());
        Assert.assertEquals(UserConstants.PASSWORD, user.getPassword());
        Assert.assertEquals(UserConstants.USERNAME, user.getName());
        Assert.assertEquals(UserConstants.PREFERRED_LANGUAGE, user.getPreferredLanguage());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", "user_id=" + user.getUserId() + " AND email='" + NON_EXISTENT_USER_EMAIL + "' AND password='" + UserConstants.PASSWORD + "' AND name='" + UserConstants.USERNAME  + "' AND preferred_language='" + UserConstants.PREFERRED_LANGUAGE + "'"));
    }

    @Test(expected = PersistenceException.class)
    public void testCreateUserWithEmailAlreadyExisting() {
        userDao.create(UserConstants.ACTIVE_USER_EMAIL, UserConstants.PASSWORD, UserConstants.USERNAME, UserConstants.PREFERRED_LANGUAGE);
        em.flush();
    }

    @Test
    @Rollback
    public void testCreateUserAddress() {
        userDao.registerAddress(UserConstants.ACTIVE_USER_ID, NON_EXISTENT_ADDRESS, NON_EXISTENT_NAME);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + NON_EXISTENT_ADDRESS + "' AND name='" + NON_EXISTENT_NAME + "'"));
        Assert.assertEquals(UserConstants.ADDRESSES_COUNT + 1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID));
    }

    @Test
    @Rollback
    public void testUpdateExistingUserAddressWithOtherName() {
        userDao.registerAddress(UserConstants.ACTIVE_USER_ID, UserConstants.LAST_USED_ADDRESS, NON_EXISTENT_NAME);

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + UserConstants.LAST_USED_ADDRESS + "' AND name='" + UserConstants.LAST_USED_ADDRESS_NAME + "'"));
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID+ " AND address='" + UserConstants.LAST_USED_ADDRESS + "' AND name='" + NON_EXISTENT_NAME + "'"));
    }

    @Test
    @Rollback
    public void testCreateExistingUserAddressWithNoName() {
        userDao.refreshAddress(UserConstants.ACTIVE_USER_ID, NON_EXISTENT_ADDRESS);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + NON_EXISTENT_ADDRESS + "' AND name IS NULL"));
    }

    @Test
    @Rollback
    public void testDeleteExistingUserAddress() {
        userDao.deleteAddress(UserConstants.ACTIVE_USER_ID, UserConstants.LAST_USED_ADDRESS);
        em.flush();

        Assert.assertEquals(UserConstants.ADDRESSES_COUNT - 1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID));
    }

    @Test
    public void testDeleteUnExistingUserAddress() {
        userDao.deleteAddress(UserConstants.ACTIVE_USER_ID, NON_EXISTENT_ADDRESS);
        em.flush();

        Assert.assertEquals(UserConstants.ADDRESSES_COUNT, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID));
    }

    @Test
    @Rollback
    public void testUpdateLastUsedUserAddress() {
        userDao.refreshAddress(UserConstants.ACTIVE_USER_ID, UserConstants.PREVIOUS_USED_ADDRESS);
        em.flush();

        // Get the tuple with max date
        Assert.assertEquals(UserConstants.PREVIOUS_USED_ADDRESS, jdbcTemplate.queryForObject("SELECT address FROM user_addresses WHERE user_id=" + UserConstants.ACTIVE_USER_ID + " AND last_used=(SELECT MAX(last_used) FROM user_addresses WHERE user_id=" + UserConstants.ACTIVE_USER_ID + ")", String.class));
    }

}
