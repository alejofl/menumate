import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.ResetPasswordTokenDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.VerificationTokenDao;
import ar.edu.itba.paw.services.TokenServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {

    @Mock
    private VerificationTokenDao verificationTokenDao;

    @Mock
    private ResetPasswordTokenDao resetPasswordTokenDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private final TokenServiceImpl tokenService = new TokenServiceImpl();

    private static final long USER_ID = 1L;
    private static final String NAME = "menumate";
    private static final String EMAIL = "menumate@menumate.com";
    private static final String TOKEN = "token";
    private static final String NEW_PASSWORD = "newPassword";
    private static final RestaurantRoleLevel LEVEL = RestaurantRoleLevel.ADMIN;

    @Before
    public void setup() {
    }

    @Test
    public void verifyUser() {
        when(verificationTokenDao.deleteTokenAndRetrieveUserId(TOKEN)).thenReturn(Optional.of(USER_ID));
        doNothing().when(userDao).updateUserActive(anyLong(), anyBoolean());
        Assert.assertTrue(tokenService.verifyUserAndDeleteVerificationToken(TOKEN));
    }

    @Test
    public void notVerifyUser() {
        when(verificationTokenDao.deleteTokenAndRetrieveUserId(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(tokenService.verifyUserAndDeleteVerificationToken(TOKEN));
    }

    @Test
    public void updatePassword() {
        when(resetPasswordTokenDao.deleteTokenAndRetrieveUserId(TOKEN)).thenReturn(Optional.of(USER_ID));
        when(passwordEncoder.encode(anyString())).thenReturn(NEW_PASSWORD);
        doNothing().when(userDao).updatePassword(anyLong(), anyString());
        Assert.assertTrue(tokenService.updatePasswordAndDeleteResetPasswordToken(TOKEN, NEW_PASSWORD));
    }

    @Test
    public void nullableVerificationToken() {
        when(verificationTokenDao.deleteTokenAndRetrieveUserId(null)).thenReturn(Optional.empty());
        Assert.assertFalse(tokenService.verifyUserAndDeleteVerificationToken(null));
    }

    @Test
    public void nullablePassword() {
        when(resetPasswordTokenDao.deleteTokenAndRetrieveUserId(TOKEN)).thenReturn(Optional.of(USER_ID));
        Assert.assertFalse(tokenService.updatePasswordAndDeleteResetPasswordToken(TOKEN, null));
    }

    @Test
    public void nullablePasswordToken() {
        when(resetPasswordTokenDao.deleteTokenAndRetrieveUserId(null)).thenReturn(Optional.empty());
        Assert.assertFalse(tokenService.updatePasswordAndDeleteResetPasswordToken(null, NEW_PASSWORD));
    }
}
