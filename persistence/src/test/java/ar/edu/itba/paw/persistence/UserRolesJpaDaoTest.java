package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.UserRoleNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRole;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserRolesJpaDaoTest {

    private static final long USER_ID_NONE = 9999;

    @Autowired
    private DataSource ds;

    @Autowired
    private UserRoleJpaDao rolesDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreate() {
        rolesDao.create(UserConstants.ACTIVE_USER_ID, UserConstants.MODERATOR_ROLE);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_roles", "user_id = " + UserConstants.ACTIVE_USER_ID + " AND role_level = " + UserConstants.MODERATOR_ROLE.ordinal()));
    }

    @Test(expected = PersistenceException.class)
    public void testCreateExistingRoleForUser() {
        rolesDao.create(UserConstants.USER_ID_MODERATOR_ROLE, UserConstants.MODERATOR_ROLE);
        em.flush();
    }

    @Test(expected = UserRoleNotFoundException.class)
    public void testDeleteThrowsWhenNonExistingRole() {
        rolesDao.delete(UserConstants.ACTIVE_USER_ID);
    }

    @Test(expected = UserRoleNotFoundException.class)
    public void testDeleteThrowsWhenNonExistingUser() {
        rolesDao.delete(USER_ID_NONE);
    }

    @Test
    @Rollback
    public void testDeleteWhenExistingRole() {
        rolesDao.delete(UserConstants.USER_ID_MODERATOR_ROLE);
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "user_roles"));
    }

    @Test
    public void testGetNoRoleOnNonExistingUser() {
        Assert.assertEquals(Optional.empty(), rolesDao.getRole(USER_ID_NONE));
    }

    @Test
    public void testGetNoRoleOnExistingUser() {
        Assert.assertEquals(Optional.empty(), rolesDao.getRole(UserConstants.ACTIVE_USER_ID));
    }

    @Test
    public void testGetRoleOnExistingUser() {
        Optional<UserRole> role = rolesDao.getRole(UserConstants.USER_ID_MODERATOR_ROLE);
        Assert.assertTrue(role.isPresent());
        Assert.assertEquals(UserConstants.MODERATOR_ROLE, role.get().getLevel());
    }

    @Test
    public void testGetByRoleOne() {
        List<User> result = rolesDao.getByRole(UserConstants.MODERATOR_ROLE);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(UserConstants.USER_ID_MODERATOR_ROLE, (long) result.get(0).getUserId());
    }

    @Test
    @Rollback
    public void testGetByRoleMany() {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(UserConstants.ACTIVE_USER_ID);
        ids.add(UserConstants.RESTAURANT_OWNER_ID);
        ids.add(UserConstants.USER_ID_MODERATOR_ROLE);
        ids.sort(Collections.reverseOrder());
        jdbcTemplate.execute("INSERT INTO user_roles (user_id, role_level) VALUES (" + UserConstants.ACTIVE_USER_ID + ", " + UserConstants.MODERATOR_ROLE.ordinal() + ")");
        jdbcTemplate.execute("INSERT INTO user_roles (user_id, role_level) VALUES (" + UserConstants.RESTAURANT_OWNER_ID + ", " + UserConstants.MODERATOR_ROLE.ordinal() + ")");

        List<User> result = rolesDao.getByRole(UserConstants.MODERATOR_ROLE);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(ids.get(0).longValue(), (long) result.get(0).getUserId());
        Assert.assertEquals(ids.get(1).longValue(), (long) result.get(1).getUserId());
    }
}
