package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    private static final long ID = 1;
    private static final String USERNAME = "pedroIsGreat";
    private static final String PASSWORD = "super12secret34";
    private static final String EMAIL = "peter@peter.com";

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);

        // All tests start with an empty users table
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    public void testFindById() throws SQLException {
        // 1. Preconditions
        jdbcTemplate.execute("INSERT INTO users (user_id, username, password, email) VALUES (" + ID + ", '" + USERNAME + "', '" + PASSWORD + "', '" + EMAIL + "')");

        // 2. Execute
        Optional<User> maybeUser = userDao.getById(ID);

        // 3. Assertions
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(ID, maybeUser.get().getUserId());
        Assert.assertEquals(USERNAME, maybeUser.get().getUsername());
        Assert.assertEquals(PASSWORD, maybeUser.get().getPassword());
        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
    }

    @Test
    public void testFindByIdDoesNotExist() throws SQLException {
        // 1. Preconditions

        // 2. Execute
        Optional<User> maybeUser = userDao.getById(ID);

        // 3. Assertions
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testCreate() {
        // 1. Preconditions

        // 2. Execute
        User user = userDao.create("thomas", PASSWORD, "thomas@thomas.com");

        // 3. Assertions
        Assert.assertNotNull(user);
        Assert.assertEquals("thomas", user.getUsername());
        Assert.assertEquals(PASSWORD, user.getPassword());
        Assert.assertEquals("thomas@thomas.com", user.getEmail());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
    }
}