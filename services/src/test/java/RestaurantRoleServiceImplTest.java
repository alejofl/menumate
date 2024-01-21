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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantRoleServiceImplTest {

    @Mock
    private RestaurantRoleDao restaurantRoleDao;

    @Mock
    private RestaurantDao restaurantDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private final RestaurantRoleServiceImpl restaurantRoleServiceImpl = new RestaurantRoleServiceImpl();

    private static final long DEFAULT_USER_ID = 123L;
    private static final long DEFAULT_RESTAURANT_ID = 456L;
    private static final String DEFAULT_USER_EMAIL = "user@user.com";
    private static final RestaurantRoleLevel ORDER_HANDLER_ROLE = RestaurantRoleLevel.ORDER_HANDLER;
    private static final RestaurantRoleLevel ADMIN_ROLE = RestaurantRoleLevel.ADMIN;
    private static final long OTHER_USER_ID = 789L;

    @Test
    public void testGetRoleExistingRole() {
        final RestaurantRole restaurantRole = mock(RestaurantRole.class);
        when(restaurantRole.getLevel()).thenReturn(ORDER_HANDLER_ROLE);
        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurantRole));

        final Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);

        assertTrue(actualRoleLevel.isPresent());
        assertEquals(ORDER_HANDLER_ROLE, actualRoleLevel.get());
    }

    @Test
    public void testGetRoleOwnerRole() {
        final Restaurant restaurant = mock(Restaurant.class);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        when(restaurant.getOwnerUserId()).thenReturn(DEFAULT_USER_ID);

        final Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);

        assertTrue(actualRoleLevel.isPresent());
        assertEquals(RestaurantRoleLevel.OWNER, actualRoleLevel.get());
    }

    @Test
    public void testGetRoleNoRoleDueToInvalidRestaurant() {
        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());

        final Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);
        assertFalse(actualRoleLevel.isPresent());
    }

    @Test
    public void testGetRoleNoRole() {
        final Restaurant restaurant = mock(Restaurant.class);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        when(restaurant.getOwnerUserId()).thenReturn(OTHER_USER_ID);

        final Optional<RestaurantRoleLevel> actualRoleLevel = restaurantRoleServiceImpl.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID);
        assertFalse(actualRoleLevel.isPresent());
    }

    @Test
    public void updateRoleOfExistingUser() {
        final User existingUser = mock(User.class);

        final RestaurantRole existingRole = spy(RestaurantRole.class);
        existingRole.setLevel(ORDER_HANDLER_ROLE);

        when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(existingUser));
        when(restaurantRoleDao.getRole(existingUser.getUserId(), DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRole));

        restaurantRoleServiceImpl.setRole(DEFAULT_USER_EMAIL, DEFAULT_RESTAURANT_ID, ADMIN_ROLE);

        assertEquals(ADMIN_ROLE, existingRole.getLevel());
    }

    @Test(expected = UserNotFoundException.class)
    public void attemptToCreateUserAndSetNullRole() {
        when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.empty());
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
        final Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getOwnerUserId()).thenReturn(DEFAULT_USER_ID);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        assertTrue(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, RestaurantRoleLevel.OWNER));
    }

    @Test
    public void testUserHasSufficientRoleLevel() {
        final RestaurantRole role = mock(RestaurantRole.class);
        when(role.getLevel()).thenReturn(ORDER_HANDLER_ROLE);

        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(role));

        assertTrue(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, ORDER_HANDLER_ROLE));
    }

    @Test
    public void testUserDoesNotHaveSufficientRoleLevel() {
        final RestaurantRole role = mock(RestaurantRole.class);
        when(role.getLevel()).thenReturn(ORDER_HANDLER_ROLE);

        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(role));

        assertFalse(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, ADMIN_ROLE));
    }

    @Test
    public void testUserRoleLevelNotAvailable() {
        final Restaurant restaurant = mock(Restaurant.class);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        when(restaurantRoleDao.getRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());

        assertFalse(restaurantRoleServiceImpl.doesUserHaveRole(DEFAULT_USER_ID, DEFAULT_RESTAURANT_ID, ADMIN_ROLE));
    }

}
