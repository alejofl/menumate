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
    private static final int ID = 791;
    private static final String EMAIL = "peter@peter.com";
    private static final String PASSWORD = "super12secret34";
    private static final String NAME = "Peter Parker";
    private static final boolean IS_ACTIVE = true;

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    public void testFindById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active) VALUES (" + ID + ", '" + EMAIL + "', '" + PASSWORD + "', '" + NAME + "', " + IS_ACTIVE + ")");

        Optional<User> maybeUser = userDao.getById(ID);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(ID, maybeUser.get().getUserId());
        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(NAME, maybeUser.get().getName());
        Assert.assertEquals(IS_ACTIVE, maybeUser.get().getIsActive());
    }

    @Test
    public void testFindByEmail() throws SQLException {
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active) VALUES (" + ID + ", '" + EMAIL + "', '" + PASSWORD + "', '" + NAME + "', " + IS_ACTIVE + ")");

        Optional<User> maybeUser = userDao.getByEmail(EMAIL);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(ID, maybeUser.get().getUserId());
        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(NAME, maybeUser.get().getName());
        Assert.assertEquals(IS_ACTIVE, maybeUser.get().getIsActive());
    }

    @Test
    public void testFindByIdDoesNotExist() throws SQLException {
        Optional<User> maybeUser = userDao.getById(ID);
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testCreate() {
        User user = userDao.create(EMAIL, PASSWORD, NAME);

        Assert.assertNotNull(user);
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(NAME, user.getName());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
    }
}