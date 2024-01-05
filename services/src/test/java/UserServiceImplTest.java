import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.TokenService;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private final UserServiceImpl userServiceImpl = new UserServiceImpl();

    private static final long USER_ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String TOKEN = "token";
    private static final String ADDRESS = "address";


    @Before
    public void setUp() {
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
    }

    @Test
    public void testCreateOrConsolidateNewUser() {
        final User user = mock(User.class);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPassword()).thenReturn(PASSWORD);
        when(user.getName()).thenReturn(USERNAME);

        final Token userVerificationToken = mock(Token.class);

        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userDao.create(EMAIL, PASSWORD, USERNAME, LocaleContextHolder.getLocale().getLanguage())).thenReturn(user);
        when(tokenService.manageUserToken(eq(user))).thenReturn(userVerificationToken);

        final User result = userServiceImpl.createOrConsolidate(EMAIL, PASSWORD, USERNAME);

        assertEquals(EMAIL, result.getEmail());
        assertEquals(PASSWORD, result.getPassword());
        assertEquals(USERNAME, result.getName());
    }

    @Test
    public void testConsolidate() {
        final User user = spy(User.class);
        user.setName(null);
        user.setPassword(null);
        user.setEmail(EMAIL);
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));

        final Token token = mock(Token.class);
        when(tokenService.manageUserToken(eq(user))).thenReturn(token);
        when(token.getToken()).thenReturn(TOKEN);

        final User result = userServiceImpl.createOrConsolidate(EMAIL, PASSWORD, USERNAME);

        assertEquals(PASSWORD, result.getPassword());
        assertEquals(USERNAME, result.getName());
        assertEquals(EMAIL, result.getEmail());
    }

    @Test(expected = IllegalStateException.class)
    public void testConsolidateExistingUser() {
        final User existingUser = mock(User.class);
        when(existingUser.getPassword()).thenReturn(PASSWORD);
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        userServiceImpl.createOrConsolidate(EMAIL, PASSWORD, USERNAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsolidateExistingUserWithNullPassword() {
        final User consolidated = spy(User.class);
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(consolidated));

        userServiceImpl.createOrConsolidate(EMAIL, null, USERNAME);
    }

    @Test
    public void testCreateIfNotExistsUserExists() {
        final User existingUser = spy(User.class);
        existingUser.setEmail(EMAIL);
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        final User result = userServiceImpl.createIfNotExists(EMAIL, USERNAME);
        assertEquals(existingUser, result);
    }

    @Test
    public void testCreateIfNotExistsUserDoesNotExist() {
        final User createdUser = mock(User.class);
        when(userDao.create(EMAIL, null, USERNAME, LocaleContextHolder.getLocale().getLanguage())).thenReturn(createdUser);
        when(createdUser.getEmail()).thenReturn(EMAIL);
        when(createdUser.getName()).thenReturn(USERNAME);
        when(createdUser.getPassword()).thenReturn(PASSWORD);
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());

        final User result = userServiceImpl.createIfNotExists(EMAIL, USERNAME);

        assertEquals(EMAIL, result.getEmail());
        assertEquals(USERNAME, result.getName());
        assertEquals(PASSWORD, result.getPassword());
    }

    @Test
    public void testIsUserEmailRegisteredAndConsolidatedUserExistsConsolidated() {
        final User user = mock(User.class);
        when(user.getPassword()).thenReturn(PASSWORD);

        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertTrue(userServiceImpl.isUserEmailRegisteredAndConsolidated(EMAIL));
    }

    @Test
    public void testIsUserEmailRegisteredAndConsolidatedUserExistsNotConsolidated() {
        final User user = mock(User.class);
        when(user.getPassword()).thenReturn(null);

        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertFalse(userServiceImpl.isUserEmailRegisteredAndConsolidated(EMAIL));
    }

    @Test
    public void testIsUserEmailRegisteredAndConsolidatedUserDoesNotExist() {
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());

        assertFalse(userServiceImpl.isUserEmailRegisteredAndConsolidated(EMAIL));
    }

    @Test
    public void testVerifyUserAndDeleteVerificationToken() {
        final Token userToken = mock(Token.class);
        when(tokenService.getByToken(TOKEN)).thenReturn(Optional.of(userToken));

        User user = mock(User.class);
        when(user.getIsActive()).thenReturn(false);

        when(userToken.getUser()).thenReturn(user);

        assertTrue(userServiceImpl.verifyUser(TOKEN).isPresent());
    }

    @Test
    public void testVerifyUserAndDeleteVerificationTokenTokenNotFound() {
        when(tokenService.getByToken(TOKEN)).thenReturn(Optional.empty());
        assertFalse(userServiceImpl.verifyUser(TOKEN).isPresent());
    }

    @Test
    public void testVerifyUserAndDeleteVerificationTokenUserAlreadyActive() {
        final Token userToken = spy(Token.class);
        final User user = mock(User.class);
        userToken.setUser(user);

        when(tokenService.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        when(user.getIsActive()).thenReturn(true);

        assertFalse(userServiceImpl.verifyUser(TOKEN).isPresent());
    }

    @Test
    public void testUpdatePassword() {
        final Token userToken = spy(Token.class);
        final User user = mock(User.class);
        userToken.setUser(user);

        when(userDao.getById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        doNothing().when(user).setPassword(anyString());

        assertTrue(userServiceImpl.updatePassword(USER_ID, PASSWORD));
    }

    @Test
    public void testUpdatePasswordUserNotFound() {
        assertFalse(userServiceImpl.updatePassword(USER_ID, PASSWORD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePasswordNullNewPassword() {
        userServiceImpl.updatePassword(USER_ID, null);
    }
}
