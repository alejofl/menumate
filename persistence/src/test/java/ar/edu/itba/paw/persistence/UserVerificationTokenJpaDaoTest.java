package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationToken;
import ar.edu.itba.paw.persistance.UserVerificationTokenDao;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserVerificationTokenJpaDaoTest {

    private static final long ID1 = 791;
    private static final long ID2 = 691;
    private static final String EMAIL1 = "peter@peter.com";
    private static final String EMAIL2 = "pedro@pedro.com";
    private static final String PASSWORD1 = "super12secret34";
    private static final String PASSWORD2 = "secret12super34";
    private static final String NAME1 = "Peter Parker";
    private static final String NAME2 = "Pedro Estacionador";
    private static final String TOKEN1 = "8ac27000-c568-4070-b6da-1a80478c";
    private static final String TOKEN2 = "3ab27010-c538-4180-a6pa-1b80581c";
    private static final boolean IS_ACTIVE = true;
    private static final String PREFERRED_LANGUAGE = "qx";
    private static final LocalDateTime EXPIRES = LocalDateTime.now().plusDays(1);
    private static User user1;

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_verification_tokens", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + ID1 + ", '" + EMAIL1 + "', '" + PASSWORD1 + "', '" + NAME1 + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + ID2 + ", '" + EMAIL2 + "', '" + PASSWORD2 + "', '" + NAME2 + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        user1 = em.find(User.class, ID1);
    }

    @Test
    public void testCreate() {
        UserVerificationToken token = verificationDao.create(user1, TOKEN1, EXPIRES);
        em.flush();
        Assert.assertNotNull(token);
        Assert.assertEquals(TOKEN1, token.getToken());
        Assert.assertEquals(ID1, token.getUser().getUserId().intValue());
        Assert.assertEquals(ID1, token.getUserId());
        Assert.assertEquals(EXPIRES, token.getExpires());
    }

    @Test(expected = PersistenceException.class)
    public void testCreateWithExistingToken() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(EXPIRES) + "')");
        UserVerificationToken token = verificationDao.create(user1, TOKEN1, EXPIRES.plusDays(1));
        em.flush();
    }

    @Test
    public void testDelete() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(EXPIRES) + "')");
        UserVerificationToken token = em.find(UserVerificationToken.class, ID1);

        verificationDao.delete(token);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_verification_tokens", "token = '" + TOKEN1 + "'"));
    }

    @Test
    public void testDeleteNonExistent() {
        UserVerificationToken token = new UserVerificationToken(user1, TOKEN1, EXPIRES);
        verificationDao.delete(token);
        em.flush();
    }

    @Test
    public void getByTokenTest() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(EXPIRES) + "')");
        Optional<UserVerificationToken> token = verificationDao.getByToken(TOKEN1);

        Assert.assertTrue(token.isPresent());
        Assert.assertEquals(TOKEN1, token.get().getToken());
        Assert.assertEquals(ID1, token.get().getUser().getUserId().intValue());
        Assert.assertEquals(ID1, token.get().getUserId());
        Assert.assertEquals(EXPIRES, token.get().getExpires());
    }

    @Test
    public void getByTokenNonExistentTest() {
        Optional<UserVerificationToken> token = verificationDao.getByToken(TOKEN2);
        Assert.assertFalse(token.isPresent());
    }

    @Test
    public void getByUserIdTest() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(EXPIRES) + "')");
        Optional<UserVerificationToken> token = verificationDao.getByUserId(ID1);

        Assert.assertTrue(token.isPresent());
        Assert.assertEquals(TOKEN1, token.get().getToken());
        Assert.assertEquals(ID1, token.get().getUser().getUserId().intValue());
        Assert.assertEquals(ID1, token.get().getUserId());
        Assert.assertEquals(EXPIRES, token.get().getExpires());
    }

    @Test
    public void getByUserIdNonExistentTest() {
        Optional<UserVerificationToken> token = verificationDao.getByUserId(ID2);
        Assert.assertFalse(token.isPresent());
    }

    @Test
    public void testDeleteStaledNoTokens() {
        verificationDao.deleteStaledTokens();
        em.flush();
    }

    @Test
    public void testDeleteStaledNoExpiredTokens() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        em.flush();
        Assert.assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_verification_tokens WHERE expires > now()", Integer.class).intValue());
    }

    @Test
    public void testDeleteStaledSingleToken() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        em.flush();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE expires <= now())", Boolean.class));
    }

    @Test
    public void testDeleteStaledTwoExpiredTokens() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        em.flush();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE expires <= now())", Boolean.class));
    }

    @Test
    public void testDeleteStaledOneExpiredOneValid() {
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_tokens (user_id, token, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        em.flush();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE user_id = " + ID1 + ")", Boolean.class));
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_tokens WHERE user_id = " + ID2 + ")", Boolean.class));
    }
}