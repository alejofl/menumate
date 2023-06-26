import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserRoleDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.services.UserRoleServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserRoleServiceImplTest {

    @Mock
    private UserRoleDao userRoleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private final UserRoleServiceImpl userRoleService = new UserRoleServiceImpl();

    private static final long DEFAULT_USER_ID = 123L;
    private static final long OTHER_USER_ID = 789L;
    private static final String DEFAULT_USER_EMAIL = "user@user.com";
    private static final UserRoleLevel MODERATOR_ROLE = UserRoleLevel.MODERATOR;

    @Test
    public void userHasRole() {
        UserRole userRole = Mockito.mock(UserRole.class);
        Mockito.when(userRole.getLevel()).thenReturn(MODERATOR_ROLE);
        Mockito.when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.of(userRole));

        Assert.assertTrue(userRoleService.doesUserHaveRole(DEFAULT_USER_ID, MODERATOR_ROLE));
    }

    @Test
    public void userDoesNotHaveRole() {
        Mockito.when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.empty());
        Assert.assertFalse(userRoleService.doesUserHaveRole(DEFAULT_USER_ID, UserRoleLevel.MODERATOR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesUserHaveRoleNullRole() {
        userRoleService.doesUserHaveRole(DEFAULT_USER_ID, null);
    }

    @Test
    public void getUserExistingRole() {
        UserRole userRole = Mockito.mock(UserRole.class);
        Mockito.when(userRole.getLevel()).thenReturn(MODERATOR_ROLE);
        Mockito.when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.of(userRole));

        final Optional<UserRoleLevel> actualRoleLevel = userRoleService.getRole(DEFAULT_USER_ID);

        Assert.assertTrue(actualRoleLevel.isPresent());
        Assert.assertEquals(MODERATOR_ROLE, actualRoleLevel.get());
    }

    @Test
    public void getUserNotExistingRole() {
        Mockito.when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.empty());

        final Optional<UserRoleLevel> actualRoleLevel = userRoleService.getRole(DEFAULT_USER_ID);

        Assert.assertFalse(actualRoleLevel.isPresent());
    }

    @Test
    public void setExistingUserExistingRole() {
        User user = Mockito.mock(User.class);
        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(user));

        Assert.assertTrue(userRoleService.setRole(DEFAULT_USER_EMAIL, MODERATOR_ROLE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setExistingUserNotExistingRole() {
        Assert.assertFalse(userRoleService.setRole(DEFAULT_USER_EMAIL, null));
    }

    @Test
    public void setNotExistingUserExistingRole() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserId()).thenReturn(DEFAULT_USER_ID);
        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userDao.create(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString())).thenReturn(user);
        Assert.assertTrue(userRoleService.setRole(DEFAULT_USER_EMAIL, MODERATOR_ROLE));
    }

    @Test
    public void setExistingUserSameRole() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserId()).thenReturn(DEFAULT_USER_ID);

        UserRole userRole = Mockito.spy(UserRole.class);
        userRole.setLevel(MODERATOR_ROLE);
        userRole.setUserId(DEFAULT_USER_ID);

        Mockito.when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.of(userRole));
        Mockito.when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(user));

        Assert.assertTrue(userRoleService.setRole(DEFAULT_USER_EMAIL, MODERATOR_ROLE));
        Assert.assertEquals(MODERATOR_ROLE, userRole.getLevel());
        Assert.assertEquals(DEFAULT_USER_ID, userRole.getUserId());
    }
}
