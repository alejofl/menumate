package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.UserAddressNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.UserConstants;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserJpaDaoTest {
    private static final long NON_EXISTENT_USER_ID = 5000;
    private static final String NON_EXISTENT_USER_EMAIL = "nonexistent@localhost";
    private static final String NON_EXISTENT_ADDRESS = "NON_EXISTENT_ADDRESS";
    private static final long NON_EXISTENT_ADDRESS_ID = 1421414;
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
    public void testFindActiveUserById() {
        final Optional<User> maybeUser = userDao.getById(UserConstants.ACTIVE_USER_ID);

        assertTrue(maybeUser.isPresent());
        assertEquals(UserConstants.ACTIVE_USER_ID, maybeUser.get().getUserId().longValue());
        assertEquals(UserConstants.ACTIVE_USER_EMAIL, maybeUser.get().getEmail());
        assertEquals(UserConstants.USERNAME, maybeUser.get().getName());
        assertTrue(maybeUser.get().getIsActive());
        assertEquals(UserConstants.PREFERRED_LANGUAGE, maybeUser.get().getPreferredLanguage());
    }

    @Test
    public void testFindNonActiveUserById() {
        final Optional<User> maybeUser = userDao.getById(UserConstants.INACTIVE_USER_ID);

        assertTrue(maybeUser.isPresent());
        assertEquals(UserConstants.INACTIVE_USER_ID, maybeUser.get().getUserId().longValue());
        assertEquals(UserConstants.INACTIVE_USER_EMAIL, maybeUser.get().getEmail());
        assertEquals(UserConstants.USERNAME, maybeUser.get().getName());
        assertFalse(maybeUser.get().getIsActive());
        assertEquals(UserConstants.PREFERRED_LANGUAGE, maybeUser.get().getPreferredLanguage());
    }

    @Test
    public void testFindActiveUserByEmail() {
        final Optional<User> maybeUser = userDao.getByEmail(UserConstants.ACTIVE_USER_EMAIL);

        assertTrue(maybeUser.isPresent());
        assertEquals(UserConstants.ACTIVE_USER_ID, maybeUser.get().getUserId().longValue());
        assertEquals(UserConstants.ACTIVE_USER_EMAIL, maybeUser.get().getEmail());
        assertEquals(UserConstants.USERNAME, maybeUser.get().getName());
        assertTrue(maybeUser.get().getIsActive());
        assertEquals(UserConstants.PREFERRED_LANGUAGE, maybeUser.get().getPreferredLanguage());
        assertEquals(LocalDateTime.now().getDayOfYear(), maybeUser.get().getDateJoined().getDayOfYear());
    }

    @Test
    public void testFindByIdDoesNotExist() {
        final Optional<User> maybeUser = userDao.getById(NON_EXISTENT_USER_ID);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindByEmailDoesNotExist() {
        final Optional<User> maybeUser = userDao.getByEmail(NON_EXISTENT_USER_EMAIL);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    @Rollback
    public void testCreate() {
        final User user = userDao.create(NON_EXISTENT_USER_EMAIL, UserConstants.PASSWORD, UserConstants.USERNAME, UserConstants.PREFERRED_LANGUAGE);
        em.flush();

        assertNotNull(user);
        assertEquals(NON_EXISTENT_USER_EMAIL, user.getEmail());
        assertEquals(UserConstants.PASSWORD, user.getPassword());
        assertEquals(UserConstants.USERNAME, user.getName());
        assertEquals(UserConstants.PREFERRED_LANGUAGE, user.getPreferredLanguage());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", "user_id=" + user.getUserId() + " AND email='" + NON_EXISTENT_USER_EMAIL + "' AND password='" + UserConstants.PASSWORD + "' AND name='" + UserConstants.USERNAME + "' AND preferred_language='" + UserConstants.PREFERRED_LANGUAGE + "'"));
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

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + NON_EXISTENT_ADDRESS + "' AND name='" + NON_EXISTENT_NAME + "'"));
        assertEquals(UserConstants.ADDRESSES_COUNT + 1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID));
    }

    @Test(expected = InvalidUserArgumentException.class)
    @Rollback
    public void testRegisterExistingUserAddressWithOtherName() {
        userDao.registerAddress(UserConstants.ACTIVE_USER_ID, UserConstants.LAST_USED_ADDRESS, NON_EXISTENT_NAME);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + UserConstants.LAST_USED_ADDRESS + "' AND name='" + NON_EXISTENT_NAME + "'"));
    }

    @Test
    @Rollback
    public void testUpdateExistingUserAddressWithOtherName() {
        userDao.updateAddress(UserConstants.ACTIVE_USER_ID, UserConstants.LAST_USED_ADDRESS_ID, UserConstants.LAST_USED_ADDRESS, NON_EXISTENT_NAME);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + UserConstants.LAST_USED_ADDRESS + "' AND name='" + NON_EXISTENT_NAME + "'"));
    }

    @Test
    @Rollback
    public void testCreateExistingUserAddressWithNoName() {
        userDao.refreshAddress(UserConstants.ACTIVE_USER_ID, NON_EXISTENT_ADDRESS);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID + " AND address='" + NON_EXISTENT_ADDRESS + "' AND name IS NULL"));
    }

    @Test
    @Rollback
    public void testDeleteExistingUserAddress() {
        userDao.deleteAddress(UserConstants.ACTIVE_USER_ID, UserConstants.LAST_USED_ADDRESS_ID);
        em.flush();

        assertEquals(UserConstants.ADDRESSES_COUNT - 1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID));
    }

    @Test(expected = UserAddressNotFoundException.class)
    public void testDeleteNonExistingUserAddress() {
        userDao.deleteAddress(UserConstants.ACTIVE_USER_ID, NON_EXISTENT_ADDRESS_ID);
        em.flush();

        assertEquals(UserConstants.ADDRESSES_COUNT, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_addresses", "user_id=" + UserConstants.ACTIVE_USER_ID));
    }

    @Test
    @Rollback
    public void testUpdateLastUsedUserAddress() {
        userDao.refreshAddress(UserConstants.ACTIVE_USER_ID, UserConstants.PREVIOUS_USED_ADDRESS);
        em.flush();

        // Get the tuple with max date
        assertEquals(UserConstants.PREVIOUS_USED_ADDRESS, jdbcTemplate.queryForObject("SELECT address FROM user_addresses WHERE user_id=" + UserConstants.ACTIVE_USER_ID + " AND last_used=(SELECT MAX(last_used) FROM user_addresses WHERE user_id=" + UserConstants.ACTIVE_USER_ID + ")", String.class));
    }

}
