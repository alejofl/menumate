package ar.edu.itba.paw.persistence;

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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserVerificationTokenJpaDaoTest {
/*
// TODO: Fix tests

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

    @Autowired
    private DataSource ds;

    @Autowired
    private UserVerificationTokenJpaDao verificationDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_verification_codes", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + ID1 + ", '" + EMAIL1 + "', '" + PASSWORD1 + "', '" + NAME1 + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + ID2 + ", '" + EMAIL2 + "', '" + PASSWORD2 + "', '" + NAME2 + "')");
    }

    @Test
    public void testCreateTokenWhenNonexisting() {
        final String token = verificationDao.generateToken(ID1);
        Assert.assertNotNull(token);
    }

    @Test
    public void testCreateTokenWhenExisting() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");

        final String token = verificationDao.generateToken(ID1);

        Assert.assertNotNull(token);
        Assert.assertEquals(token, jdbcTemplate.queryForObject("SELECT code FROM user_verification_codes WHERE user_id = " + ID1, String.class));
    }

    @Test
    public void testCreateTokenWhenExpired() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");

        final String token = verificationDao.generateToken(ID1);

        Assert.assertNotNull(token);
        Assert.assertEquals(token, jdbcTemplate.queryForObject("SELECT code FROM user_verification_codes WHERE user_id = " + ID1, String.class));
    }

    @Test
    public void testHasActiveTokenExisting() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertTrue(verificationDao.hasActiveToken(ID1));
    }

    @Test
    public void testHasActiveTokenNonexisting() {
        Assert.assertFalse(verificationDao.hasActiveToken(ID1));
    }

    @Test
    public void testHasActiveTokenExpired() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertFalse(verificationDao.hasActiveToken(ID1));
    }

    @Test
    public void testDeleteStaledNoTokens() {
        verificationDao.deleteStaledTokens();
    }

    @Test
    public void testDeleteStaledNoExpiredTokens() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        Assert.assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_verification_codes WHERE expires > now()", Integer.class).intValue());
    }

    @Test
    public void testDeleteStaledSingleToken() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_codes WHERE expires <= now())", Boolean.class));
    }

    @Test
    public void testDeleteStaledTwoExpiredTokens() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_codes WHERE expires <= now())", Boolean.class));
    }

    @Test
    public void testDeleteStaledOneExpiredOneValid() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        verificationDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_codes WHERE user_id = " + ID1 + ")", Boolean.class));
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_verification_codes WHERE user_id = " + ID2 + ")", Boolean.class));
    }

    @Test
    public void testIsValidTokenNoTokens() {
        Assert.assertFalse(verificationDao.isValidToken(TOKEN1));
        Assert.assertFalse(verificationDao.isValidToken(TOKEN2));
    }

    @Test
    public void testIsValidTokenOneValidOneNonexisting() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertTrue(verificationDao.isValidToken(TOKEN1));
        Assert.assertFalse(verificationDao.isValidToken(TOKEN2));
    }

    @Test
    public void testIsValidTokenOneValidOneExpired() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertTrue(verificationDao.isValidToken(TOKEN1));
        Assert.assertFalse(verificationDao.isValidToken(TOKEN2));
    }

    @Test
    public void testDeleteGetIdWhenExisting() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");

        Optional<Long> userId = verificationDao.deleteTokenAndRetrieveUserId(TOKEN1);
        Assert.assertTrue(userId.isPresent());
        Assert.assertEquals(ID1, userId.get().longValue());
    }

    @Test
    public void testDeleteGetIdWhenNonexisting() {
        Optional<Long> userId = verificationDao.deleteTokenAndRetrieveUserId(TOKEN1);
        Assert.assertFalse(userId.isPresent());
    }

    @Test
    public void testDeleteGetIdWhenExpired() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");

        Optional<Long> userId = verificationDao.deleteTokenAndRetrieveUserId(TOKEN1);
        Assert.assertFalse(userId.isPresent());
    }

    @Test
    public void testDeleteGetIdWhenOneValidOneNonexisting() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");

        Optional<Long> userId1 = verificationDao.deleteTokenAndRetrieveUserId(TOKEN1);
        Optional<Long> userId2 = verificationDao.deleteTokenAndRetrieveUserId(TOKEN2);

        Assert.assertTrue(userId1.isPresent());
        Assert.assertEquals(ID1, userId1.get().longValue());
        Assert.assertFalse(userId2.isPresent());
    }

    @Test
    public void testDeleteGetIdWhenOneValidOneExpired() {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");

        Optional<Long> userId1 = verificationDao.deleteTokenAndRetrieveUserId(TOKEN1);
        Optional<Long> userId2 = verificationDao.deleteTokenAndRetrieveUserId(TOKEN2);

        Assert.assertTrue(userId1.isPresent());
        Assert.assertEquals(ID1, userId1.get().longValue());
        Assert.assertFalse(userId2.isPresent());
    }
    */
}
