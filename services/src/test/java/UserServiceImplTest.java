import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationToken;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserVerificationTokenDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private UserVerificationTokenDao verificationTokenDao;

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
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
    }

    @Test
    public void testCreateOrConsolidateNewUser() {
        final User user = mock(User.class);
        Mockito.when(user.getEmail()).thenReturn(EMAIL);
        Mockito.when(user.getPassword()).thenReturn(PASSWORD);
        Mockito.when(user.getName()).thenReturn(USERNAME);

        final UserVerificationToken userVerificationToken = mock(UserVerificationToken.class);

        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userDao.create(EMAIL, PASSWORD, USERNAME, LocaleContextHolder.getLocale().getLanguage())).thenReturn(user);
        Mockito.when(verificationTokenDao.create(eq(user), anyString(), any())).thenReturn(userVerificationToken);

        final User result = userServiceImpl.createOrConsolidate(EMAIL, PASSWORD, USERNAME);

        Assert.assertEquals(EMAIL, result.getEmail());
        Assert.assertEquals(PASSWORD, result.getPassword());
        Assert.assertEquals(USERNAME, result.getName());
    }

    @Test
    public void testConsolidate() {
        final User user = Mockito.spy(User.class);
        user.setName(null);
        user.setPassword(null);
        user.setEmail(EMAIL);
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserVerificationToken token = mock(UserVerificationToken.class);
        Mockito.when(verificationTokenDao.create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(token);
        Mockito.when(token.getToken()).thenReturn(TOKEN);

        final User result = userServiceImpl.createOrConsolidate(EMAIL, PASSWORD, USERNAME);

        Assert.assertEquals(PASSWORD, result.getPassword());
        Assert.assertEquals(USERNAME, result.getName());
        Assert.assertEquals(EMAIL, result.getEmail());
    }

    @Test(expected = IllegalStateException.class)
    public void testConsolidateExistingUser() {
        final User existingUser = mock(User.class);
        Mockito.when(existingUser.getPassword()).thenReturn(PASSWORD);
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        userServiceImpl.createOrConsolidate(EMAIL, PASSWORD, USERNAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsolidateExistingUserWithNullPassword() {
        final User consolidated = Mockito.spy(User.class);
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(consolidated));

        userServiceImpl.createOrConsolidate(EMAIL, null, USERNAME);
    }

    @Test
    public void testCreateIfNotExistsUserExists() {
        final User existingUser = Mockito.spy(User.class);
        existingUser.setEmail(EMAIL);
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        final User result = userServiceImpl.createIfNotExists(EMAIL, USERNAME);
        Assert.assertEquals(existingUser, result);
    }

    @Test
    public void testCreateIfNotExistsUserDoesNotExist() {
        final User createdUser = mock(User.class);
        Mockito.when(userDao.create(EMAIL, null, USERNAME, LocaleContextHolder.getLocale().getLanguage())).thenReturn(createdUser);
        Mockito.when(createdUser.getEmail()).thenReturn(EMAIL);
        Mockito.when(createdUser.getName()).thenReturn(USERNAME);
        Mockito.when(createdUser.getPassword()).thenReturn(PASSWORD);
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());

        final User result = userServiceImpl.createIfNotExists(EMAIL, USERNAME);

        Assert.assertEquals(EMAIL, result.getEmail());
        Assert.assertEquals(USERNAME, result.getName());
        Assert.assertEquals(PASSWORD, result.getPassword());
    }

    @Test
    public void testIsUserEmailRegisteredAndConsolidatedUserExistsConsolidated() {
        final User user = mock(User.class);
        Mockito.when(user.getPassword()).thenReturn(PASSWORD);

        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));

        Assert.assertTrue(userServiceImpl.isUserEmailRegisteredAndConsolidated(EMAIL));
    }

    @Test
    public void testIsUserEmailRegisteredAndConsolidatedUserExistsNotConsolidated() {
        final User user = mock(User.class);
        Mockito.when(user.getPassword()).thenReturn(null);

        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));

        Assert.assertFalse(userServiceImpl.isUserEmailRegisteredAndConsolidated(EMAIL));
    }

    @Test
    public void testIsUserEmailRegisteredAndConsolidatedUserDoesNotExist() {
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());

        Assert.assertFalse(userServiceImpl.isUserEmailRegisteredAndConsolidated(EMAIL));
    }
}
