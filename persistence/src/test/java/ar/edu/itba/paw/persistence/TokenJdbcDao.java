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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TokenJdbcDao {

    private static final long ID = 791;
    private static final String EMAIL = "peter@peter.com";
    private static final String PASSWORD = "super12secret34";
    private static final String NEWPASSWORD = "newpassword";
    private static final String NAME = "Peter Parker";
    private static final String TOKEN = UUID.randomUUID().toString().substring(0, 32);

    @Autowired
    private DataSource ds;

    @Autowired
    private ResetPasswordTokenJdbcDao resetPasswordTokenDao;

    @Autowired
    private VerificationTokenJdbcDao verificationTokenJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_resetpassword_codes", "user_resetpassword_codes", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + ID + ", '" + EMAIL + "', '" + PASSWORD + "', '" + NAME + "')");
    }

    @Test
    public void testCreateResetPasswordToken() throws SQLException {
        final String token = resetPasswordTokenDao.generateToken(ID);
        Assert.assertNotNull(token);
    }

    @Test
    public void testCreateVerificationToken() throws SQLException {
        final String token = verificationTokenJdbcDao.generateToken(ID);
        Assert.assertNotNull(token);
    }

    @Test
    public void testHasActiveResetPasswordToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertTrue(resetPasswordTokenDao.hasActiveToken(ID));
    }

    @Test
    public void testHasActiveVerificationToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertTrue(verificationTokenJdbcDao.hasActiveToken(ID));
    }

    @Test
    public void testNoActiveVerificationToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertFalse(verificationTokenJdbcDao.hasActiveToken(ID));
    }

    @Test
    public void testNoActiveResetPasswordToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertFalse(resetPasswordTokenDao.hasActiveToken(ID));
    }

    @Test
    public void testDeleteStaledVerificationToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        verificationTokenJdbcDao.deleteStaledTokens();
        Assert.assertFalse(verificationTokenJdbcDao.hasActiveToken(ID));
    }

    @Test
    public void testDeleteStaledResetPasswordToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertFalse(resetPasswordTokenDao.hasActiveToken(ID));
    }

    @Test
    public void testNoDeleteValidVerificationToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        verificationTokenJdbcDao.deleteStaledTokens();
        Assert.assertTrue(verificationTokenJdbcDao.hasActiveToken(ID));
    }

    @Test
    public void testNoDeleteValidResetPasswordToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertTrue(resetPasswordTokenDao.hasActiveToken(ID));
    }

    @Test
    public void testValidResetPasswordToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertTrue(resetPasswordTokenDao.isValidToken(TOKEN));
    }

    @Test
    public void testValidVerificationToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertTrue(verificationTokenJdbcDao.isValidToken(TOKEN));
    }

    @Test
    public void testInvalidResetPasswordToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertFalse(resetPasswordTokenDao.isValidToken(TOKEN));
    }

    @Test
    public void testInvalidVerificationToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertFalse(verificationTokenJdbcDao.isValidToken(TOKEN));
    }

    @Test
    public void testUpdatePasswordAndDeleteToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertEquals(PASSWORD, jdbcTemplate.queryForObject("SELECT password FROM users WHERE user_id = " + ID, String.class));
        Assert.assertTrue(resetPasswordTokenDao.hasActiveToken(ID));
        Assert.assertTrue(resetPasswordTokenDao.updatePasswordAndDeleteToken(TOKEN, NEWPASSWORD));
        Assert.assertFalse(resetPasswordTokenDao.hasActiveToken(ID));
        Assert.assertEquals(NEWPASSWORD, jdbcTemplate.queryForObject("SELECT password FROM users WHERE user_id = " + ID, String.class));
    }

    @Test
    public void testNoUpdatePasswordAndDeleteToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertEquals(PASSWORD, jdbcTemplate.queryForObject("SELECT password FROM users WHERE user_id = " + ID, String.class));
        Assert.assertFalse(resetPasswordTokenDao.hasActiveToken(ID));
        Assert.assertFalse(resetPasswordTokenDao.updatePasswordAndDeleteToken(TOKEN, NEWPASSWORD));
        Assert.assertFalse(resetPasswordTokenDao.hasActiveToken(ID));
        Assert.assertEquals(PASSWORD, jdbcTemplate.queryForObject("SELECT password FROM users WHERE user_id = " + ID, String.class));
    }
    @Test
    public void testUpdatePasswordAndDeleteTokenWithNoEntry() throws SQLException {
        Assert.assertFalse(resetPasswordTokenDao.updatePasswordAndDeleteToken(TOKEN, NEWPASSWORD));
    }

    @Test
    public void testUpdatePasswordAndDeleteTokenWithNoToken() throws SQLException {
        Assert.assertFalse(resetPasswordTokenDao.updatePasswordAndDeleteToken(null, NEWPASSWORD));
    }

    @Test
    public void testUpdatePasswordAndDeleteTokenWithNoPassword() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertFalse(resetPasswordTokenDao.updatePasswordAndDeleteToken(TOKEN, null));
    }

    @Test
    public void testVerifyAndDeleteToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT is_active FROM users WHERE user_id = " + ID, Boolean.class));
        Assert.assertTrue(verificationTokenJdbcDao.hasActiveToken(ID));
        Assert.assertTrue(verificationTokenJdbcDao.verifyUserAndDeleteToken(TOKEN));
        Assert.assertFalse(verificationTokenJdbcDao.hasActiveToken(ID));
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT is_active FROM users WHERE user_id = " + ID, Boolean.class));
    }

    @Test
    public void testNoVerifyAndDeleteToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_verification_codes (user_id, code, expires) VALUES (" + ID + ", '" + TOKEN + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT is_active FROM users WHERE user_id = " + ID, Boolean.class));
        Assert.assertFalse(verificationTokenJdbcDao.hasActiveToken(ID));
        Assert.assertFalse(verificationTokenJdbcDao.verifyUserAndDeleteToken(TOKEN));
        Assert.assertFalse(verificationTokenJdbcDao.hasActiveToken(ID));
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT is_active FROM users WHERE user_id = " + ID, Boolean.class));
    }

    @Test
    public void testNoVerifyAndDeleteTokenWithNoEntry() throws SQLException {
        Assert.assertFalse(verificationTokenJdbcDao.verifyUserAndDeleteToken(TOKEN));
    }

    @Test
    public void testNoVerifyAndDeleteTokenWithNoToken() throws SQLException {
        Assert.assertFalse(verificationTokenJdbcDao.verifyUserAndDeleteToken(null));
    }
}
