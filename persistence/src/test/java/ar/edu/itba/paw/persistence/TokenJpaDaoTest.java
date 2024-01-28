package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.TokenNotFoundException;
import ar.edu.itba.paw.model.Token;
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
    public void testCreateToken() {
        final User inactiveUser = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final Token tkn = tokenDao.create(
                inactiveUser,
                UserConstants.UNUSED_TOKEN,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        assertNotNull(tkn);
        assertEquals(inactiveUser.getUserId(), tkn.getUser().getUserId());
        assertEquals(UserConstants.UNUSED_TOKEN, tkn.getToken());
        assertEquals(UserConstants.TOKEN_EXPIRATION.getDayOfYear(), tkn.getExpiryDate().getDayOfYear());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + inactiveUser.getUserId() + " AND token = '" + UserConstants.UNUSED_TOKEN + "'"));
    }

    @Test
    @Rollback
    public void testRefreshToken() {
        final Token token = em.find(Token.class, UserConstants.TOKEN2_ID);
        final Token tkn = tokenDao.refresh(
                token,
                UserConstants.UNUSED_TOKEN,
                UserConstants.TOKEN_EXPIRATION
        );
        em.flush();

        assertNotNull(tkn);
        assertEquals(UserConstants.USER_ID_2_WITH_TOKENS, tkn.getUser().getUserId().longValue());
        assertEquals(token.getTokenId(), tkn.getTokenId());
        assertEquals(UserConstants.UNUSED_TOKEN, tkn.getToken());
        assertTrue(tkn.isFresh());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "token_id = " + tkn.getTokenId() + " AND user_id = " + tkn.getUser().getUserId() + " AND token = '" + UserConstants.UNUSED_TOKEN + "'"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "token_id = " + tkn.getTokenId() + " AND user_id = " + tkn.getUser().getUserId() + " AND token = '" + UserConstants.TOKEN2 + "'"));
    }

    @Test
    public void testGetTokenByToken() {
        final User userWithToken = em.find(User.class, UserConstants.USER_ID_1_WITH_TOKENS);
        final Optional<Token> maybeToken = tokenDao.getByToken(UserConstants.TOKEN1);

        assertTrue(maybeToken.isPresent());
        final Token token = maybeToken.get();

        assertEquals(token.getToken(), UserConstants.TOKEN1);
        assertEquals(token.getUser().getUserId(), userWithToken.getUserId());
    }

    @Test
    public void testGetNoTokenByToken() {
        final Optional<Token> tkn = tokenDao.getByToken(UserConstants.UNUSED_TOKEN);
        assertFalse(tkn.isPresent());
    }

    @Test
    public void testGetTokenByUserId() {
        final User userWithToken = em.find(User.class, UserConstants.USER_ID_1_WITH_TOKENS);
        final Optional<Token> maybeToken = tokenDao.getByUserId(userWithToken.getUserId());

        assertTrue(maybeToken.isPresent());
        final Token token = maybeToken.get();

        assertEquals(token.getToken(), UserConstants.TOKEN1);
        assertEquals(token.getUser().getUserId(), userWithToken.getUserId());
    }

    @Test
    public void testGetNoTokenByUserId() {
        final User userWithToken = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final Optional<Token> maybeToken = tokenDao.getByUserId(userWithToken.getUserId());
        assertFalse(maybeToken.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteToken() {
        final Token token = em.find(Token.class, UserConstants.TOKEN2_ID);

        tokenDao.delete(token);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "user_id = " + UserConstants.USER_ID_2_WITH_TOKENS + " AND token_id = '" + UserConstants.TOKEN2_ID + "'"));
    }

    @Test(expected = TokenNotFoundException.class)
    public void testDeleteNoToken() {
        final User userWithNoToken = em.find(User.class, UserConstants.INACTIVE_USER_ID);
        final Token tkn = new Token(userWithNoToken, UserConstants.UNUSED_TOKEN, UserConstants.TOKEN_EXPIRATION);
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
        final Token token = em.find(Token.class, UserConstants.TOKEN1_ID);
        token.setExpiryDate(LocalDateTime.now().minusDays(5));

        tokenDao.deleteStaledTokens();
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM tokens WHERE expiryDate <= now())", Boolean.class));
    }

    @Test
    @Rollback
    public void testDeleteStaledOneExpiredOneValid() {
        final Token tkn = em.find(Token.class, UserConstants.TOKEN1_ID);
        tkn.setExpiryDate(LocalDateTime.now().minusDays(5));

        tokenDao.deleteStaledTokens();
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM tokens WHERE user_id = ? AND token = ?)", Boolean.class, UserConstants.USER_ID_1_WITH_TOKENS, UserConstants.TOKEN1));
        assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM tokens WHERE user_id = ? AND token = ?)", Boolean.class, UserConstants.USER_ID_2_WITH_TOKENS, UserConstants.TOKEN2));
    }
}
