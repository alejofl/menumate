import ar.edu.itba.paw.exception.UserRoleNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserRoleDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.services.UserRoleServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        UserRole userRole = mock(UserRole.class);
        when(userRole.getLevel()).thenReturn(MODERATOR_ROLE);
        when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.of(userRole));

        assertTrue(userRoleService.doesUserHaveRole(DEFAULT_USER_ID, MODERATOR_ROLE));
    }

    @Test
    public void userDoesNotHaveRole() {
        when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.empty());
        assertFalse(userRoleService.doesUserHaveRole(DEFAULT_USER_ID, UserRoleLevel.MODERATOR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesUserHaveRoleNullRole() {
        userRoleService.doesUserHaveRole(DEFAULT_USER_ID, null);
    }

    @Test
    public void getUserExistingRole() {
        UserRole userRole = mock(UserRole.class);
        when(userRole.getLevel()).thenReturn(MODERATOR_ROLE);
        when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.of(userRole));

        final Optional<UserRoleLevel> actualRoleLevel = userRoleService.getRole(DEFAULT_USER_ID);

        assertTrue(actualRoleLevel.isPresent());
        assertEquals(MODERATOR_ROLE, actualRoleLevel.get());
    }

    @Test
    public void getUserNotExistingRole() {
        when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.empty());

        final Optional<UserRoleLevel> actualRoleLevel = userRoleService.getRole(DEFAULT_USER_ID);

        assertFalse(actualRoleLevel.isPresent());
    }

    @Test
    public void setExistingUserExistingRole() {
        User user = mock(User.class);
        when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(user));

        assertTrue(userRoleService.setRole(DEFAULT_USER_EMAIL, MODERATOR_ROLE));
    }

    @Test(expected = UserRoleNotFoundException.class)
    public void setExistingUserNotExistingRole() {
        assertFalse(userRoleService.setRole(DEFAULT_USER_EMAIL, null));
    }

    @Test
    public void setNotExistingUserExistingRole() {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(DEFAULT_USER_ID);
        when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.empty());
        when(userDao.create(anyString(), any(), anyString(), anyString())).thenReturn(user);
        assertTrue(userRoleService.setRole(DEFAULT_USER_EMAIL, MODERATOR_ROLE));
    }

    @Test
    public void setExistingUserSameRole() {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(DEFAULT_USER_ID);

        UserRole userRole = spy(UserRole.class);
        userRole.setLevel(MODERATOR_ROLE);
        userRole.setUserId(DEFAULT_USER_ID);

        when(userRoleDao.getRole(DEFAULT_USER_ID)).thenReturn(Optional.of(userRole));
        when(userDao.getByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.of(user));

        assertTrue(userRoleService.setRole(DEFAULT_USER_EMAIL, MODERATOR_ROLE));
        assertEquals(MODERATOR_ROLE, userRole.getLevel());
        assertEquals(DEFAULT_USER_ID, userRole.getUserId());
    }
}
