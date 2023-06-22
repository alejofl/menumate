package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserResetpasswordToken;
import ar.edu.itba.paw.persistance.UserResetpasswordTokenDao;
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
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserResetPasswordTokenJpaDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserResetpasswordTokenDao resetPasswordTokenDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateForInactiveUser() {
        final User inactiveUser = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final UserResetpasswordToken urpt = resetPasswordTokenDao.create(
                inactiveUser,
                UserConstants.TOKEN1,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        Assert.assertNotNull(urpt);
        Assert.assertEquals(inactiveUser.getUserId(), urpt.getUser().getUserId());
        Assert.assertEquals(inactiveUser.getUserId().longValue(), urpt.getUserId());
        Assert.assertEquals(UserConstants.TOKEN1, urpt.getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), urpt.getExpires().getDayOfYear());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + inactiveUser.getUserId() + " AND token = '" + UserConstants.TOKEN1 + "'"));
    }

    @Test
    @Rollback
    public void testCreateForActiveUser() {
        User activeUser = em.find(User.class, UserConstants.ACTIVE_USER_ID);
        UserResetpasswordToken urpt = resetPasswordTokenDao.create(
                activeUser,
                UserConstants.TOKEN2,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        Assert.assertNotNull(urpt);
        Assert.assertEquals(activeUser.getUserId(), urpt.getUser().getUserId());
        Assert.assertEquals(activeUser.getUserId().longValue(), urpt.getUserId());
        Assert.assertEquals(UserConstants.TOKEN2, urpt.getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), urpt.getExpires().getDayOfYear());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + activeUser.getUserId() + " AND token = '" + UserConstants.TOKEN2 + "'"));
    }

    @Test(expected = PersistenceException.class)
    @Rollback
    public void testCreateForUserWithActiveToken() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        UserResetpasswordToken urpt = resetPasswordTokenDao.create(
                userWithToken,
                UserConstants.TOKEN1,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();
    }

    @Test
    public void testGetByUserId() {
        final Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByUserId(UserConstants.USER_ID_WITH_TOKENS);

        Assert.assertTrue(urpt.isPresent());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, urpt.get().getUserId());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, urpt.get().getUser().getUserId().longValue());
        Assert.assertEquals(UserConstants.ACTIVE_RESET_PASSWORD_TOKEN, urpt.get().getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), urpt.get().getExpires().getDayOfYear());
    }

    @Test
    public void testGetByUserIdNoActiveToken() {
        final Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByUserId(UserConstants.INACTIVE_USER_ID);
        Assert.assertFalse(urpt.isPresent());
    }

    @Test
    public void testGetByToken() {
        final Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByToken(UserConstants.ACTIVE_RESET_PASSWORD_TOKEN);

        Assert.assertTrue(urpt.isPresent());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, urpt.get().getUserId());
        Assert.assertEquals(UserConstants.USER_ID_WITH_TOKENS, urpt.get().getUser().getUserId().longValue());
        Assert.assertEquals(UserConstants.ACTIVE_RESET_PASSWORD_TOKEN, urpt.get().getToken());
        Assert.assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), urpt.get().getExpires().getDayOfYear());
    }

    @Test
    public void testGetByTokenNoActiveToken() {
        final Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByToken(UserConstants.TOKEN1);
        Assert.assertFalse(urpt.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteToken() {
        final UserResetpasswordToken urpt = em.find(UserResetpasswordToken.class, UserConstants.USER_ID_WITH_TOKENS);

        resetPasswordTokenDao.delete(urpt);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + UserConstants.USER_ID_WITH_TOKENS + " AND token = '" + UserConstants.ACTIVE_RESET_PASSWORD_TOKEN + "'"));
    }

    @Test
    public void testDeleteNoToken() {
        final User user = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final UserResetpasswordToken urpt = new UserResetpasswordToken(user, UserConstants.ACTIVE_RESET_PASSWORD_TOKEN, UserConstants.TOKEN_EXPIRATION);
        resetPasswordTokenDao.delete(urpt);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + UserConstants.INACTIVE_USER_ID));
    }


    @Test
    @Rollback
    public void testDeleteStaledNoTokens() {
        resetPasswordTokenDao.deleteStaledTokens();
        em.flush();
    }

    @Test
    public void testDeleteStaledNoExpiredTokens() {
        resetPasswordTokenDao.deleteStaledTokens();
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "expires > now()"));
    }

    @Test
    @Rollback
    public void testDeleteStaledSingleToken() {
        final UserResetpasswordToken urpt = em.find(UserResetpasswordToken.class, UserConstants.USER_ID_WITH_TOKENS);
        urpt.setExpires(LocalDateTime.now().minusDays(5));

        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE expires <= now())", Boolean.class));
    }

    @Test
    @Rollback
    public void testDeleteStaledOneExpiredOneValid() {
        final UserResetpasswordToken urpt = em.find(UserResetpasswordToken.class, UserConstants.USER_ID_WITH_TOKENS);
        urpt.setExpires(LocalDateTime.now().minusDays(5));
        final UserResetpasswordToken urpt2 = new UserResetpasswordToken(em.find(User.class, UserConstants.INACTIVE_USER_ID), UserConstants.TOKEN1, UserConstants.TOKEN_EXPIRATION);
        em.persist(urpt2);

        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE user_id = " + UserConstants.USER_ID_WITH_TOKENS + ")", Boolean.class));
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE user_id = " + UserConstants.INACTIVE_USER_ID + ")", Boolean.class));
    }
}