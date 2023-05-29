import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserResetpasswordTokenDao;
import ar.edu.itba.paw.persistance.UserVerificationTokenDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {
/*
// TODO: Fix tests

    @Mock
    private UserVerificationTokenDao verificationTokenDao;

    @Mock
    private UserResetpasswordTokenDao resetPasswordTokenDao;

    @Mock
    private UserDao userDao;

    @Mock
    private UserDetailsService userDetailsService;

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
        doNothing().when(userDao).updateIsActive(any(User.class), anyBoolean());
        when(userDao.getById(USER_ID)).thenReturn(Optional.of(new User(USER_ID, EMAIL, NAME, null, true, null)));
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(null);
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
        doNothing().when(userDao).updatePassword(any(User.class), anyString());
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
    }*/
}
