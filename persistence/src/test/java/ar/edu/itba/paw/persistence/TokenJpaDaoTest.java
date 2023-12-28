package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.TokenType;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.TokenDao;
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
public class TokenJpaDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private TokenDao tokenDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateResetPasswordToken() {
        final User inactiveUser = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final Token tkn = tokenDao.create(
                inactiveUser,
                TokenType.RESET_PASSWORD_TOKEN,
                UserConstants.UNUSED_TOKEN,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        assertNotNull(tkn);
        assertEquals(inactiveUser.getUserId(), tkn.getUser().getUserId());
        assertEquals(inactiveUser.getUserId(), tkn.getUser().getUserId());
        assertEquals(UserConstants.UNUSED_TOKEN, tkn.getToken());
        assertEquals(TokenType.RESET_PASSWORD_TOKEN, tkn.getType());
        assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), tkn.getExpiryDate().getDayOfYear());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + inactiveUser.getUserId() + " AND token = '" + UserConstants.UNUSED_TOKEN + "'"));
    }

    @Test
    @Rollback
    public void testCreateVerifyToken() {
        final User inactiveUser = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final Token tkn = tokenDao.create(
                inactiveUser,
                TokenType.VERIFICATION_TOKEN,
                UserConstants.UNUSED_TOKEN,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        assertNotNull(tkn);
        assertEquals(inactiveUser.getUserId(), tkn.getUser().getUserId());
        assertEquals(inactiveUser.getUserId(), tkn.getUser().getUserId());
        assertEquals(UserConstants.UNUSED_TOKEN, tkn.getToken());
        assertEquals(TokenType.VERIFICATION_TOKEN, tkn.getType());
        assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), tkn.getExpiryDate().getDayOfYear());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + inactiveUser.getUserId() + " AND token = '" + UserConstants.UNUSED_TOKEN + "'"));
    }

    @Test
    @Rollback
    public void testMergeActiveResetPasswordToken() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        Token tkn = tokenDao.create(
                userWithToken,
                TokenType.RESET_PASSWORD_TOKEN,
                UserConstants.TOKEN1,
                UserConstants.TOKEN_EXPIRATION
        );
        assertEquals(UserConstants.TOKEN1, tkn.getToken());
        em.flush();
    }

    @Test
    @Rollback
    public void testMergeActiveVerificationToken() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        Token tkn = tokenDao.create(
                userWithToken,
                TokenType.VERIFICATION_TOKEN,
                UserConstants.TOKEN2,
                UserConstants.TOKEN_EXPIRATION
        );

        assertEquals(UserConstants.TOKEN2, tkn.getToken());
        em.flush();
    }

    @Test
    public void testGetResetPasswordByToken() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        Optional<Token> maybeToken = tokenDao.getByToken(UserConstants.TOKEN2);

        assertTrue(maybeToken.isPresent());
        Token token = maybeToken.get();

        assertEquals(token.getToken(), UserConstants.TOKEN2);
        assertEquals(token.getUser().getUserId(), userWithToken.getUserId());
        assertEquals(token.getType(), TokenType.RESET_PASSWORD_TOKEN);
    }

    @Test
    public void testGetVerifyByToken() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        Optional<Token> maybeToken = tokenDao.getByToken(UserConstants.TOKEN1);

        assertTrue(maybeToken.isPresent());
        Token token = maybeToken.get();

        assertEquals(token.getToken(), UserConstants.TOKEN1);
        assertEquals(token.getUser().getUserId(), userWithToken.getUserId());
        assertEquals(token.getType(), TokenType.VERIFICATION_TOKEN);
    }

    @Test
    public void testGetNoTokenByToken() {
        final Optional<Token> tkn = tokenDao.getByToken(UserConstants.UNUSED_TOKEN);
        assertFalse(tkn.isPresent());
    }

    @Test
    public void testGetVerifyTokenByUserId() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        Optional<Token> maybeToken = tokenDao.getByUserId(userWithToken.getUserId(), TokenType.VERIFICATION_TOKEN);

        assertTrue(maybeToken.isPresent());
        Token token = maybeToken.get();

        assertEquals(token.getToken(), UserConstants.TOKEN1);
        assertEquals(token.getUser().getUserId(), userWithToken.getUserId());
        assertEquals(token.getType(), TokenType.VERIFICATION_TOKEN);
    }

    @Test
    public void testGetResetPasswordTokenByUserId() {
        User userWithToken = em.find(User.class, UserConstants.USER_ID_WITH_TOKENS);
        Optional<Token> maybeToken = tokenDao.getByUserId(userWithToken.getUserId(), TokenType.RESET_PASSWORD_TOKEN);

        assertTrue(maybeToken.isPresent());
        Token token = maybeToken.get();

        assertEquals(token.getToken(), UserConstants.TOKEN2);
        assertEquals(token.getUser().getUserId(), userWithToken.getUserId());
        assertEquals(token.getType(), TokenType.RESET_PASSWORD_TOKEN);
    }

    @Test
    public void testGetNoResetPasswordTokenByUserId() {
        User userWithToken = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        Optional<Token> maybeToken = tokenDao.getByUserId(userWithToken.getUserId(), TokenType.RESET_PASSWORD_TOKEN);
        assertFalse(maybeToken.isPresent());
    }

    @Test
    public void testGetNoVerificationTokenByUserId() {
        User userWithToken = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        Optional<Token> maybeToken = tokenDao.getByUserId(userWithToken.getUserId(), TokenType.VERIFICATION_TOKEN);
        assertFalse(maybeToken.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteVerifyToken() {
        final Token token = em.find(Token.class, UserConstants.TOKEN1);

        tokenDao.delete(token);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + UserConstants.USER_ID_WITH_TOKENS + " AND token = '" + UserConstants.TOKEN1 + "'"));
    }

    @Test
    @Rollback
    public void testDeleteResetPasswordToken() {
        final Token token = em.find(Token.class, UserConstants.TOKEN2);

        tokenDao.delete(token);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + UserConstants.USER_ID_WITH_TOKENS + " AND token = '" + UserConstants.TOKEN2 + "'"));
    }

    @Test
    public void testDeleteNoToken() {
        final User userWithNoToken = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final Token tkn = new Token(userWithNoToken, TokenType.VERIFICATION_TOKEN, UserConstants.UNUSED_TOKEN, UserConstants.TOKEN_EXPIRATION);
        tokenDao.delete(tkn);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + UserConstants.INACTIVE_USER_ID));
    }

    @Test
    @Rollback
    public void testDeleteStaledNoTokens() {
        tokenDao.deleteStaledTokens();
        em.flush();
    }

    @Test
    public void testDeleteStaledNoExpiredTokens() {
        tokenDao.deleteStaledTokens();
        em.flush();
        assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "expiryDate > now()"));
    }

    @Test
    @Rollback
    public void testDeleteStaledSingleToken() {
        final Token token = em.find(Token.class, UserConstants.TOKEN1);
        token.setExpiryDate(LocalDateTime.now().minusDays(5));

        tokenDao.deleteStaledTokens();
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM tokens WHERE expiryDate <= now())", Boolean.class));
    }

    @Test
    @Rollback
    public void testDeleteStaledOneExpiredOneValid() {
        final Token tkn = em.find(Token.class, UserConstants.TOKEN1);
        tkn.setExpiryDate(LocalDateTime.now().minusDays(5));

        tokenDao.deleteStaledTokens();
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM tokens WHERE user_id = ? AND token = ?)", Boolean.class, UserConstants.USER_ID_WITH_TOKENS, UserConstants.TOKEN1));
        assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM tokens WHERE user_id = ? AND token = ?)", Boolean.class, UserConstants.USER_ID_WITH_TOKENS, UserConstants.TOKEN2));
    }
}
