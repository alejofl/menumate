import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRole;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.persistance.RestaurantRoleDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.services.RestaurantRoleServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.*;

import javax.mail.MessagingException;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantRoleServiceTest {

    @Mock
    private RestaurantRoleDao restaurantRoleDao;

    @Mock
    private RestaurantDao restaurantDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RestaurantRoleServiceImpl restaurantRoleServiceImpl = new RestaurantRoleServiceImpl();

    private static final long DEFAULT_USER_ID = 123L;
    private static final long DEFAULT_RESTAURANT_ID = 456L;
    private static final String DEFAULT_USER_EMAIL = "user@user.com";
    private static final RestaurantRoleLevel ORDER_HANDLER_ROLE = RestaurantRoleLevel.ORDER_HANDLER;
    private static final RestaurantRoleLevel ADMIN_ROLE = RestaurantRoleLevel.ADMIN;
    private static final long OTHER_USER_ID = 789L;

    @Test
    public void testGetRoleExistingRole() {
        RestaurantRole restaurantRole = Mockito.mock(RestaurantRole.class);
        Mockito.when(restaurantRole.getLevel()).thenReturn(ORDER_HANDLER_ROLE);
        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurantRole));

        Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);

        Assert.assertTrue(actualRoleLevel.isPresent());
        Assert.assertEquals(ORDER_HANDLER_ROLE, actualRoleLevel.get());
    }

    @Test
    public void testGetRoleOwnerRole() {
        Restaurant restaurant = Mockito.mock(Restaurant.class);
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        Mockito.when(restaurant.getOwnerUserId()).thenReturn(DEFAULT_USER_ID);

        Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);

        Assert.assertTrue(actualRoleLevel.isPresent());
        Assert.assertEquals(RestaurantRoleLevel.OWNER, actualRoleLevel.get());
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testGetRoleNoRoleDueToInvalidRestaurant() {
        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());

        restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);
    }

    @Test
    public void testGetRoleNoRole() {
        Restaurant restaurant = Mockito.mock(Restaurant.class);
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        Mockito.when(restaurant.getOwnerUserId()).thenReturn(OTHER_USER_ID);

        Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);
        Assert.assertFalse(actualRoleLevel.isPresent());
    }

    @Test
    public void createUserAndSetsRole() throws MessagingException {
        User user = Mockito.mock(User.class);
        Restaurant restaurant = Mockito.mock(Restaurant.class);

        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userDao.create(Mockito.eq(DEFAULT_USER_EMAIL), Mockito.eq(null), Mockito.anyString(), Mockito.anyString())).thenReturn(user);
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        restaurantRoleServiceImpl.setRole(DEFAULT_USER_EMAIL, DEFAULT_RESTAURANT_ID, ORDER_HANDLER_ROLE);

        Mockito.verify(userDao).create(Mockito.anyString(), Mockito.eq(null), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(restaurantRoleDao).create(Mockito.anyLong(), Mockito.eq(DEFAULT_RESTAURANT_ID), Mockito.eq(ORDER_HANDLER_ROLE));
        Mockito.verify(emailService).sendInvitationToRestaurantStaff(Mockito.any(User.class), Mockito.any(Restaurant.class));
    }

    @Test
    public void deleteRoleOfExistingUser() {
        User existingUser = Mockito.mock(User.class);
        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(existingUser));

        restaurantRoleServiceImpl.setRole(DEFAULT_USER_EMAIL, DEFAULT_RESTAURANT_ID, null);

        Mockito.verify(restaurantRoleDao).delete(existingUser.getUserId(), DEFAULT_RESTAURANT_ID);
    }

    @Test
    public void updateRoleOfExistingUser() {
        User existingUser = Mockito.mock(User.class);

        RestaurantRole existingRole = Mockito.spy(RestaurantRole.class);
        existingRole.setLevel(ORDER_HANDLER_ROLE);

        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(existingUser));
        Mockito.when(restaurantRoleDao.getRole(existingUser.getUserId(), DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRole));

        restaurantRoleServiceImpl.setRole(DEFAULT_USER_EMAIL, DEFAULT_RESTAURANT_ID, ADMIN_ROLE);

        Assert.assertEquals(ADMIN_ROLE, existingRole.getLevel());
    }

    @Test(expected = UserNotFoundException.class)
    public void attemptToCreateUserAndSetNullRole() {
        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.empty());
        restaurantRoleServiceImpl.setRole(DEFAULT_USER_EMAIL, DEFAULT_RESTAURANT_ID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void attemptToSetOwnerRole() {
        restaurantRoleServiceImpl.setRole(DEFAULT_USER_EMAIL, DEFAULT_RESTAURANT_ID, RestaurantRoleLevel.OWNER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMinimumRoleLevel() {
        restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, null);
    }

    @Test
    public void testUserHasOwnerRole() {
        Restaurant restaurant = Mockito.mock(Restaurant.class);
        Mockito.when(restaurant.getOwnerUserId()).thenReturn(DEFAULT_USER_ID);
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        Assert.assertTrue(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, RestaurantRoleLevel.OWNER));
    }

    @Test
    public void testUserHasSufficientRoleLevel() {
        RestaurantRole role = Mockito.mock(RestaurantRole.class);
        Mockito.when(role.getLevel()).thenReturn(ORDER_HANDLER_ROLE);

        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(role));

        Assert.assertTrue(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, ORDER_HANDLER_ROLE));
    }

    @Test
    public void testUserDoesNotHaveSufficientRoleLevel() {
        RestaurantRole role = Mockito.mock(RestaurantRole.class);
        Mockito.when(role.getLevel()).thenReturn(ORDER_HANDLER_ROLE);

        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(role));

        Assert.assertFalse(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, ADMIN_ROLE));
    }

    @Test
    public void testUserRoleLevelNotAvailable() {
        Restaurant restaurant = Mockito.mock(Restaurant.class);

        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        Mockito.when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());

        Assert.assertFalse(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, ADMIN_ROLE));
    }

}
