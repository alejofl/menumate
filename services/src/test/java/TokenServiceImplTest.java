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

import javax.mail.MessagingException;
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
    public void testSendUserVerificationTokenUserIsActive() throws MessagingException {
        final User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(true);

        userServiceImpl.sendUserVerificationToken(user);

        Mockito.verify(verificationTokenDao, Mockito.never()).getByUserId(Mockito.anyLong());
        Mockito.verify(verificationTokenDao, Mockito.never()).create(Mockito.any(User.class), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        Mockito.verify(emailService, Mockito.never()).sendUserVerificationEmail(Mockito.any(User.class), Mockito.anyString());
    }

    @Test
    public void testSendUserVerificationTokenInactiveUserNoExistingToken() throws MessagingException {
        final User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(false);
        Mockito.when(user.getUserId()).thenReturn(USER_ID);

        Mockito.when(verificationTokenDao.getByUserId(user.getUserId())).thenReturn(Optional.empty());

        UserVerificationToken token = mock(UserVerificationToken.class);
        Mockito.when(verificationTokenDao.create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(token);
        Mockito.when(token.getToken()).thenReturn(TOKEN);

        userServiceImpl.sendUserVerificationToken(user);

        Mockito.verify(verificationTokenDao).getByUserId(user.getUserId());
        Mockito.verify(verificationTokenDao).create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        Mockito.verify(emailService).sendUserVerificationEmail(Mockito.eq(user), Mockito.anyString());
    }

    @Test
    public void testSendUserVerificationTokenInactiveUserExpiredToken() throws MessagingException {
        final User user = mock(User.class);
        when(user.getIsActive()).thenReturn(false);
        when(user.getUserId()).thenReturn(USER_ID);

        UserVerificationToken expiredToken = mock(UserVerificationToken.class);
        Mockito.when(verificationTokenDao.getByUserId(user.getUserId())).thenReturn(Optional.of(expiredToken));
        Mockito.when(expiredToken.getToken()).thenReturn(TOKEN);
        Mockito.when(expiredToken.isExpired()).thenReturn(true);

        userServiceImpl.sendUserVerificationToken(user);

        Mockito.verify(verificationTokenDao, Mockito.never()).create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        Mockito.verify(verificationTokenDao).getByUserId(user.getUserId());
        Mockito.verify(expiredToken).setExpires(any());
        Mockito.verify(expiredToken).setToken(any());
        Mockito.verify(emailService).sendUserVerificationEmail(Mockito.eq(user), Mockito.anyString());
    }

    @Test
    public void testSendPasswordResetToken() throws MessagingException {
        final User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(true);
        Mockito.when(user.getUserId()).thenReturn(USER_ID);

        Mockito.when(resetPasswordTokenDao.getByUserId(user.getUserId())).thenReturn(Optional.empty());

        UserResetpasswordToken token = mock(UserResetpasswordToken.class);
        Mockito.when(resetPasswordTokenDao.create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(token);
        Mockito.when(token.getToken()).thenReturn(TOKEN);

        userServiceImpl.sendPasswordResetToken(user);

        Mockito.verify(resetPasswordTokenDao).getByUserId(user.getUserId());
        Mockito.verify(resetPasswordTokenDao).create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        Mockito.verify(emailService).sendResetPasswordEmail(Mockito.eq(user), Mockito.anyString());
    }

    @Test
    public void testSendPasswordResetTokenInactiveUser() throws MessagingException {
        final User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(false);
        Mockito.when(user.getUserId()).thenReturn(USER_ID);

        userServiceImpl.sendPasswordResetToken(user);

        Mockito.verify(resetPasswordTokenDao, Mockito.never()).getByUserId(user.getUserId());
        Mockito.verify(resetPasswordTokenDao, Mockito.never()).create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        Mockito.verify(emailService, Mockito.never()).sendResetPasswordEmail(Mockito.eq(user), Mockito.anyString());
    }

    @Test
    public void testSendPasswordResetTokenExpiredToken() throws MessagingException {
        final User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(true);
        Mockito.when(user.getUserId()).thenReturn(USER_ID);

        UserResetpasswordToken expiredToken = mock(UserResetpasswordToken.class);
        Mockito.when(resetPasswordTokenDao.getByUserId(user.getUserId())).thenReturn(Optional.of(expiredToken));
        Mockito.when(expiredToken.getToken()).thenReturn(TOKEN);
        Mockito.when(expiredToken.isExpired()).thenReturn(true);

        userServiceImpl.sendPasswordResetToken(user);

        Mockito.verify(expiredToken).setToken(Mockito.anyString());
        Mockito.verify(expiredToken).setExpires(Mockito.any(LocalDateTime.class));
        Mockito.verify(resetPasswordTokenDao).getByUserId(user.getUserId());
        Mockito.verify(resetPasswordTokenDao, Mockito.never()).create(Mockito.eq(user), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        Mockito.verify(emailService).sendResetPasswordEmail(Mockito.eq(user), Mockito.anyString());
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
