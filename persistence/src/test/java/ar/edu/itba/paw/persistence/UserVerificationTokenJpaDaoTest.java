package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserResetpasswordToken;
import ar.edu.itba.paw.model.UserVerificationToken;
import ar.edu.itba.paw.persistance.UserVerificationTokenDao;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserVerificationTokenJpaDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserVerificationTokenDao verificationDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateForInactiveUser() throws SQLException {
        final User inactiveUser = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final UserVerificationToken uvt = verificationDao.create(
                inactiveUser,
                UserConstants.TOKEN1,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        Assert.assertNotNull(uvt);
        Assert.assertEquals(inactiveUser.getUserId(), uvt.getUser().getUserId());
        Assert.assertEquals(inactiveUser.getUserId().intValue(), uvt.getUserId());
        Assert.assertEquals(UserConstants.TOKEN1, uvt.getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), uvt.getExpires().getDayOfYear());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_verification_tokens", "user_id = " + inactiveUser.getUserId() + " AND token = '" + UserConstants.TOKEN1 + "'"));
    }

    @Test
    @Rollback
    public void testCreateForActiveUser() throws SQLException {
        final User activeUser = em.find(User.class, UserConstants.ACTIVE_USER_ID);
        final UserVerificationToken uvt = verificationDao.create(
                activeUser,
                UserConstants.TOKEN2,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        Assert.assertNotNull(uvt);
        Assert.assertEquals(activeUser.getUserId(), uvt.getUser().getUserId());
        Assert.assertEquals(activeUser.getUserId().intValue(), uvt.getUserId());
        Assert.assertEquals(UserConstants.TOKEN2, uvt.getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), uvt.getExpires().getDayOfYear());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_verification_tokens", "user_id = " + activeUser.getUserId() + " AND token = '" + UserConstants.TOKEN2 + "'"));
    }

    @Test(expected = PersistenceException.class)
    @Rollback
    public void testCreateForUserWithActiveToken() throws SQLException {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        final UserVerificationToken uvt = verificationDao.create(
                userWithToken,
                UserConstants.TOKEN1,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();
    }

    @Test
    public void testGetByUserId() throws SQLException {
        final Optional<UserVerificationToken> uvt = verificationDao.getByUserId(UserConstants.USER_ID_WITH_TOKENS);

        Assert.assertTrue(uvt.isPresent());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, uvt.get().getUserId());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, uvt.get().getUser().getUserId().intValue());
        Assert.assertEquals(UserConstants.ACTIVE_VERIFICATION_TOKEN, uvt.get().getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), uvt.get().getExpires().getDayOfYear());
    }

    @Test
    public void testGetByUserIdNoActiveToken() throws SQLException {
        final Optional<UserVerificationToken> uvt = verificationDao.getByUserId(UserConstants.INACTIVE_USER_ID);
        Assert.assertFalse(uvt.isPresent());
    }

    @Test
    public void testGetByToken() throws SQLException {
        final Optional<UserVerificationToken> uvt = verificationDao.getByToken(UserConstants.ACTIVE_VERIFICATION_TOKEN);

        Assert.assertTrue(uvt.isPresent());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, uvt.get().getUserId());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, uvt.get().getUser().getUserId().intValue());
        Assert.assertEquals(UserConstants.ACTIVE_VERIFICATION_TOKEN, uvt.get().getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), uvt.get().getExpires().getDayOfYear());
    }

    @Test
    public void testGetByTokenNoActiveToken() throws SQLException {
        final Optional<UserVerificationToken> uvt = verificationDao.getByToken(UserConstants.TOKEN1);
        Assert.assertFalse(uvt.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteToken() throws SQLException {
        final UserVerificationToken uvt = em.find(UserVerificationToken.class, UserConstants.USER_ID_WITH_TOKENS);

        verificationDao.delete(uvt);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_verification_tokens", "user_id = " + UserConstants.USER_ID_WITH_TOKENS + " AND token = '" + UserConstants.ACTIVE_RESET_PASSWORD_TOKEN + "'"));
    }

    @Test
    public void testDeleteNoToken() throws SQLException {
        final User user = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final UserVerificationToken uvt = new UserVerificationToken(user, UserConstants.ACTIVE_RESET_PASSWORD_TOKEN, UserConstants.TOKEN_EXPIRATION);
        verificationDao.delete(uvt);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_verification_tokens", "user_id = " + UserConstants.INACTIVE_USER_ID));
    }


    @Test
    @Rollback
    public void testDeleteStaledNoTokens() throws SQLException {
        verificationDao.deleteStaledTokens();
        em.flush();
    }

    @Test
    public void testDeleteStaledNoExpiredTokens() throws SQLException {
        verificationDao.deleteStaledTokens();
        em.flush();
        Assert.assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_verification_tokens WHERE expires > now()", Integer.class).intValue());
    }

    @Test
    @Rollback
    public void testDeleteStaledSingleToken() throws SQLException {
        final UserVerificationToken uvt = em.find(UserVerificationToken.class, UserConstants.USER_ID_WITH_TOKENS);
        uvt.setExpires(LocalDateTime.now().minusDays(5));

        verificationDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE expires <= now())", Boolean.class));
    }

    @Test
    @Rollback
    public void testDeleteStaledOneExpiredOneValid() throws SQLException {
        final UserVerificationToken uvt = em.find(UserVerificationToken.class, UserConstants.USER_ID_WITH_TOKENS);
        uvt.setExpires(LocalDateTime.now().minusDays(5));
        final UserVerificationToken uvt2 = new UserVerificationToken(em.find(User.class, UserConstants.INACTIVE_USER_ID), UserConstants.TOKEN1, UserConstants.TOKEN_EXPIRATION);
        em.persist(uvt2);

        verificationDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE user_id = " + UserConstants.USER_ID_WITH_TOKENS + ")", Boolean.class));
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE user_id = " + UserConstants.INACTIVE_USER_ID + ")", Boolean.class));
    }
}