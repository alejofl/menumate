package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.RestaurantRole;
import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
import ar.edu.itba.paw.util.PaginatedResult;
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

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class RestaurantRoleJpaDaoTest {
    private static final long USER_ID_NONE = 9999;
    private static final long RESTAURANT_ID_NONE = 9999;


    @Autowired
    private DataSource ds;

    @Autowired
    private RestaurantRoleJpaDao rolesDao;

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
        rolesDao.create(UserConstants.ACTIVE_USER_ID, RestaurantConstants.RESTAURANT_IDS[0], UserConstants.ADMIN_ROLE);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id = " + UserConstants.ACTIVE_USER_ID + " AND restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND role_level = " + UserConstants.ADMIN_ROLE.ordinal()));
    }

    @Test(expected = PersistenceException.class)
    public void testCreateExistingRoleForRestaurant() {
        rolesDao.create(UserConstants.USER_ADMIN_ROLE, RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS, UserConstants.ADMIN_ROLE);
        em.flush();
    }

    @Test
    public void testDeleteThrowsWhenNonExistingRole() {
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(UserConstants.ACTIVE_USER_ID, RestaurantConstants.RESTAURANT_IDS[0]));
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(UserConstants.ACTIVE_USER_ID, RestaurantConstants.RESTAURANT_IDS[1]));
    }

    @Test
    public void testDeleteThrowsWhenNonExistingUser() {
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID_NONE, RestaurantConstants.RESTAURANT_IDS[0]));
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(USER_ID_NONE, RestaurantConstants.RESTAURANT_IDS[1]));
    }

    @Test
    @Rollback
    public void testDeleteWhenExisting() {
        rolesDao.delete(UserConstants.USER_ADMIN_ROLE, RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS);
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id=" + UserConstants.USER_ADMIN_ROLE + " AND restaurant_id=" + RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS + " AND role_level=" + UserConstants.ADMIN_ROLE.ordinal()));
    }

    @Test
    public void testDeleteWhenNonExistingWithOtherRolePresent() {
        Assert.assertThrows(EntityNotFoundException.class, () -> rolesDao.delete(UserConstants.USER_ADMIN_ROLE, RestaurantConstants.RESTAURANT_IDS[0]));
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_roles", "user_id=" + UserConstants.USER_ADMIN_ROLE + " AND restaurant_id=" + RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS));
    }

    @Test
    public void testGetNoRoleOnNonExistingUserNonExistingRestaurant() {
        Assert.assertFalse(rolesDao.getRole(USER_ID_NONE, RESTAURANT_ID_NONE).isPresent());
    }

    @Test
    public void testGetNoRoleOnNonExistingUser() {
        Assert.assertFalse(rolesDao.getRole(USER_ID_NONE, RestaurantConstants.RESTAURANT_IDS[0]).isPresent());
        Assert.assertFalse(rolesDao.getRole(USER_ID_NONE,  RestaurantConstants.RESTAURANT_IDS[1]).isPresent());
    }

    @Test
    public void testGetNoRoleOnNonExistingRestaurant() {
        Assert.assertFalse(rolesDao.getRole(UserConstants.ACTIVE_USER_ID, RESTAURANT_ID_NONE).isPresent());
        Assert.assertFalse(rolesDao.getRole(UserConstants.RESTAURANT_OWNER_ID, RESTAURANT_ID_NONE).isPresent());
    }

    @Test
    public void testGetNoRole() {
        Assert.assertFalse(rolesDao.getRole(UserConstants.ACTIVE_USER_ID, RestaurantConstants.RESTAURANT_IDS[0]).isPresent());
        Assert.assertFalse(rolesDao.getRole(UserConstants.ACTIVE_USER_ID, RestaurantConstants.RESTAURANT_IDS[1]).isPresent());
    }

    @Test
    public void testGetRole() {
        Optional<RestaurantRole> role = rolesDao.getRole(UserConstants.USER_ADMIN_ROLE, RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS);

        Assert.assertTrue(role.isPresent());
        Assert.assertEquals(UserConstants.ADMIN_ROLE, role.get().getLevel());
    }

    @Test
    public void testGetByRestaurantNone() {
        List<RestaurantRole> result = rolesDao.getByRestaurant(RESTAURANT_ID_NONE);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetByRestaurant() {
        List<RestaurantRole> result = rolesDao.getByRestaurant(RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(UserConstants.ADMIN_ROLE, result.get(0).getLevel());
    }

    @Test
    public void testGetByUserNone() {
        PaginatedResult<RestaurantRoleDetails> result = rolesDao.getByUser(USER_ID_NONE, 1, 10);
        Assert.assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testGetByUser() {
        PaginatedResult<RestaurantRoleDetails> result = rolesDao.getByUser(UserConstants.USER_ADMIN_ROLE, 1, 10);
        Assert.assertEquals(1, result.getTotalCount());
        Assert.assertEquals(UserConstants.ADMIN_ROLE, result.getResult().get(0).getLevel());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_ID_WITH_NO_ORDERS, result.getResult().get(0).getRestaurantId());
    }
}
