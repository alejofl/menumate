import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.TokenService;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceImplTest {

    @Mock
    private TokenService tokenService;

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

    @Test
    public void testVerifyUserAndDeleteVerificationToken() {
        final Token userToken = mock(Token.class);
        Mockito.when(tokenService.getByToken(TOKEN)).thenReturn(Optional.of(userToken));

        User user = mock(User.class);
        Mockito.when(user.getIsActive()).thenReturn(false);

        Mockito.when(userToken.getUser()).thenReturn(user);

        Assert.assertTrue(userServiceImpl.verifyUser(TOKEN).isPresent());
    }

    @Test
    public void testVerifyUserAndDeleteVerificationTokenTokenNotFound() {
        Mockito.when(tokenService.getByToken(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(userServiceImpl.verifyUser(TOKEN).isPresent());
    }

    @Test
    public void testVerifyUserAndDeleteVerificationTokenUserAlreadyActive() {
        final Token userToken = spy(Token.class);
        final User user = mock(User.class);
        userToken.setUser(user);

        Mockito.when(tokenService.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(user.getIsActive()).thenReturn(true);

        Assert.assertFalse(userServiceImpl.verifyUser(TOKEN).isPresent());
    }

    @Test
    public void testUpdatePasswordAndDeleteResetPasswordToken() {
        final Token userToken = spy(Token.class);
        final User user = mock(User.class);
        userToken.setUser(user);

        Mockito.when(tokenService.getByToken(TOKEN)).thenReturn(Optional.of(userToken));
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        Mockito.doNothing().when(user).setPassword(Mockito.anyString());

        Assert.assertTrue(userServiceImpl.updatePassword(TOKEN, PASSWORD));
    }

    @Test
    public void testUpdatePasswordAndDeleteResetPasswordTokenTokenNotFound() {
        Mockito.when(tokenService.getByToken(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(userServiceImpl.updatePassword(TOKEN, PASSWORD));
    }

    @Test
    public void testUpdatePasswordAndDeleteResetPasswordTokenExpiredToken() {
        final Token userToken = spy(Token.class);
        final User user = mock(User.class);
        userToken.setUser(user);

        Mockito.when(tokenService.getByToken(TOKEN)).thenReturn(Optional.of(userToken));

        Assert.assertTrue(userServiceImpl.updatePassword(TOKEN, PASSWORD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePasswordAndDeleteResetPasswordTokenNullNewPassword() {
        userServiceImpl.updatePassword(TOKEN, null);
    }
}
