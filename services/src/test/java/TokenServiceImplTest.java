import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserResetpasswordToken;
import ar.edu.itba.paw.model.UserVerificationToken;
import ar.edu.itba.paw.persistance.UserResetpasswordTokenDao;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceImplTest {

    @Mock
    private UserVerificationTokenDao verificationTokenDao;

    @Mock
    private UserResetpasswordTokenDao resetPasswordTokenDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private final UserServiceImpl userServiceImpl = new UserServiceImpl();

    private static final long USER_ID = 1L;
    private static final String TOKEN = "token";
    private static final String PASSWORD = "password";

    @Before
    public void setup() {
    }

    @Test
    public void testVerifyUserAndDeleteVerificationToken() {
        final UserDetails userDetails = mock(UserDetails.class);

        final UserVerificationToken userToken = mock(UserVerificationToken.class);
        Mockito.when(verificationTokenDao.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isExpired()).thenReturn(false);

        User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(false);

        Mockito.when(userToken.getUser()).thenReturn(user);
        Mockito.when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);

        Assert.assertTrue(userServiceImpl.verifyUserAndDeleteVerificationToken(TOKEN));
    }

    @Test
    public void testVerifyUserAndDeleteVerificationTokenTokenNotFound() {
        Mockito.when(verificationTokenDao.getByToken(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(userServiceImpl.verifyUserAndDeleteVerificationToken(TOKEN));
    }

    @Test
    public void testVerifyUserAndDeleteVerificationTokenUserAlreadyActive() {
        final UserVerificationToken userToken = mock(UserVerificationToken.class);
        final User user = mock(User.class);

        Mockito.when(verificationTokenDao.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isExpired()).thenReturn(false);
        Mockito.when(userToken.getUser()).thenReturn(user);
        Mockito.when(user.getIsActive()).thenReturn(true);

        Assert.assertFalse(userServiceImpl.verifyUserAndDeleteVerificationToken(TOKEN));
    }

    @Test
    public void testUpdatePasswordAndDeleteResetPasswordToken() {
        final UserResetpasswordToken userToken = mock(UserResetpasswordToken.class);
        final User user = mock(User.class);

        Mockito.when(resetPasswordTokenDao.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isExpired()).thenReturn(false);
        Mockito.when(userToken.getUser()).thenReturn(user);
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);

        Assert.assertTrue(userServiceImpl.updatePasswordAndDeleteResetPasswordToken(TOKEN, PASSWORD));
    }

    @Test
    public void testUpdatePasswordAndDeleteResetPasswordTokenTokenNotFound() {
        Mockito.when(resetPasswordTokenDao.getByToken(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(userServiceImpl.updatePasswordAndDeleteResetPasswordToken(TOKEN, PASSWORD));
    }

    @Test
    public void testUpdatePasswordAndDeleteResetPasswordTokenExpiredToken() {
        final UserResetpasswordToken userToken = mock(UserResetpasswordToken.class);
        Mockito.when(resetPasswordTokenDao.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isExpired()).thenReturn(true);

        Assert.assertFalse(userServiceImpl.updatePasswordAndDeleteResetPasswordToken(TOKEN, PASSWORD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePasswordAndDeleteResetPasswordTokenNullNewPassword() {
        userServiceImpl.updatePasswordAndDeleteResetPasswordToken(TOKEN, null);
    }

    @Test
    public void testHasActiveVerificationTokenWithActiveToken() {
        final UserVerificationToken userToken = mock(UserVerificationToken.class);
        Mockito.when(verificationTokenDao.getByUserId(USER_ID)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isFresh()).thenReturn(true);

        Assert.assertTrue(userServiceImpl.hasActiveVerificationToken(USER_ID));
    }

    @Test
    public void testHasActiveVerificationTokenWithNoToken() {
        Mockito.when(verificationTokenDao.getByUserId(USER_ID)).thenReturn(Optional.empty());
        Assert.assertFalse(userServiceImpl.hasActiveVerificationToken(USER_ID));
    }

    @Test
    public void testHasActiveVerificationTokenWithExpiredToken() {
        final UserVerificationToken userToken = mock(UserVerificationToken.class);
        Mockito.when(verificationTokenDao.getByUserId(USER_ID)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isFresh()).thenReturn(false);

        Assert.assertFalse(userServiceImpl.hasActiveVerificationToken(USER_ID));
    }

    @Test
    public void testIsValidResetPasswordTokenWithValidToken() {
        final UserResetpasswordToken userToken = mock(UserResetpasswordToken.class);
        Mockito.when(resetPasswordTokenDao.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isFresh()).thenReturn(true);

        Assert.assertTrue(userServiceImpl.isValidResetPasswordToken(TOKEN));
    }

    @Test
    public void testIsValidResetPasswordTokenWithInvalidToken() {
        Mockito.when(resetPasswordTokenDao.getByToken(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(userServiceImpl.isValidResetPasswordToken(TOKEN));
    }

    @Test
    public void testIsValidResetPasswordTokenWithExpiredToken() {
        final UserResetpasswordToken userToken = mock(UserResetpasswordToken.class);
        Mockito.when(resetPasswordTokenDao.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(userToken.isFresh()).thenReturn(false);

        Assert.assertFalse(userServiceImpl.isValidResetPasswordToken(TOKEN));
    }
}
