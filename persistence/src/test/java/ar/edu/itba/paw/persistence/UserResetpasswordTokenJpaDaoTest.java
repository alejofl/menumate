package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserResetpasswordToken;
import ar.edu.itba.paw.persistance.UserResetpasswordTokenDao;
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
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserResetpasswordTokenJpaDaoTest {
    private static final long ID1 = 791;
    private static final long ID2 = 691;
    private static final String EMAIL1 = "peter@peter.com";
    private static final String EMAIL2 = "pedro@pedro.com";
    private static final String PASSWORD1 = "super12secret34";
    private static final String PASSWORD2 = "secret12super34";
    private static final String NAME1 = "Peter Parker";
    private static final String NAME2 = "Pedro Estacionador";
    private static final String PREFERRED_LANGUAGE = "qx";
    private static final boolean IS_ACTIVE = true;
    private static final String TOKEN1 = "8ac27000-c568-4070-b6da-1a80478c";
    private static final String TOKEN2 = "3ab27010-c538-4180-a6pa-1b80581c";
    private static User user1;

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_resetpassword_tokens", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + ID1 + ", '" + EMAIL1 + "', '" + PASSWORD1 + "', '" + NAME1 + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + ID2 + ", '" + EMAIL2 + "', '" + PASSWORD2 + "', '" + NAME2 + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        user1 = em.find(User.class, ID1);
    }

    @Test
    public void testCreate() throws SQLException {
        UserResetpasswordToken urpt = resetPasswordTokenDao.create(user1, TOKEN1, LocalDateTime.now().plusDays(1));
        em.flush();

        Assert.assertNotNull(urpt);
        Assert.assertEquals(user1.getUserId(), urpt.getUser().getUserId());
        Assert.assertEquals(user1.getUserId().intValue(), urpt.getUserId());
        Assert.assertEquals(TOKEN1, urpt.getToken());
        Assert.assertEquals(LocalDateTime.now().plusDays(1).getDayOfYear(), urpt.getExpires().getDayOfYear());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + user1.getUserId() + " AND token = '" + TOKEN1 + "'"));
    }

    @Test
    public void testGetByUserId() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");

        Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByUserId(ID1);

        Assert.assertTrue(urpt.isPresent());
        Assert.assertEquals(ID1, urpt.get().getUserId());
        Assert.assertEquals(ID1, urpt.get().getUser().getUserId().intValue());
        Assert.assertEquals(TOKEN1, urpt.get().getToken());
        Assert.assertEquals(LocalDateTime.now().plusDays(1).getDayOfYear(), urpt.get().getExpires().getDayOfYear());
    }

    @Test
    public void testGetByUserIdNoToken() throws SQLException {
        Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByUserId(ID1);
        Assert.assertFalse(urpt.isPresent());
    }

    @Test
    public void testGetByToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");

        Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByToken(TOKEN1);

        Assert.assertTrue(urpt.isPresent());
        Assert.assertEquals(ID1, urpt.get().getUserId());
        Assert.assertEquals(ID1, urpt.get().getUser().getUserId().intValue());
        Assert.assertEquals(TOKEN1, urpt.get().getToken());
        Assert.assertEquals(LocalDateTime.now().plusDays(1).getDayOfYear(), urpt.get().getExpires().getDayOfYear());
    }

    @Test
    public void testGetByTokenNoToken() throws SQLException {
        Optional<UserResetpasswordToken> urpt = resetPasswordTokenDao.getByToken(TOKEN1);
        Assert.assertFalse(urpt.isPresent());
    }

    @Test
    public void testDeleteToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        final UserResetpasswordToken U = em.find(UserResetpasswordToken.class, ID1);

        resetPasswordTokenDao.delete(U);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + ID1 + " AND token = '" + TOKEN1 + "'"));
    }
    @Test
    public void testDeleteNoToken() throws SQLException {
        final UserResetpasswordToken U = new UserResetpasswordToken(user1, TOKEN1, LocalDateTime.now().plusDays(1));
        resetPasswordTokenDao.delete(U);
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_resetpassword_tokens", "user_id = " + ID1 + " AND token = '" + TOKEN1 + "'"));

    }

    @Test
    public void testDeleteStaledNoTokens() throws SQLException {
        resetPasswordTokenDao.deleteStaledTokens();
        em.flush();
    }

    @Test
    public void testDeleteStaledNoExpiredTokens() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        resetPasswordTokenDao.deleteStaledTokens();
        em.flush();
        Assert.assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_resetpassword_tokens WHERE expires > now()", Integer.class).intValue());
    }

    @Test
    public void testDeleteStaledSingleToken() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE expires <= now())", Boolean.class));
    }

    @Test
    public void testDeleteStaledTwoExpiredTokens() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE expires <= now())", Boolean.class));
    }

    @Test
    public void testDeleteStaledOneExpiredOneValid() throws SQLException {
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID1 + ", '" + TOKEN1 + "', '" + Timestamp.valueOf(LocalDateTime.now().minusDays(1)) + "')");
        jdbcTemplate.execute("INSERT INTO user_resetpassword_tokens (user_id, token, expires) VALUES (" + ID2 + ", '" + TOKEN2 + "', '" + Timestamp.valueOf(LocalDateTime.now().plusDays(1)) + "')");
        resetPasswordTokenDao.deleteStaledTokens();
        Assert.assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE user_id = " + ID1 + ")", Boolean.class));
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_resetpassword_tokens WHERE user_id = " + ID2 + ")", Boolean.class));
    }
}