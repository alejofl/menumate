package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.model.UserResetpasswordToken;
import ar.edu.itba.paw.model.UserVerificationToken;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserResetpasswordTokenDao;
import ar.edu.itba.paw.persistance.UserVerificationTokenDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Integer TOKEN_DURATION_DAYS = 1;

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserVerificationTokenDao verificationTokenDao;

    @Autowired
    private UserResetpasswordTokenDao resetpasswordTokenDao;

    @Autowired
    private EmailService emailService;

    private static String generateRandomToken() {
        return UUID.randomUUID().toString().substring(0, 32);
    }

    private static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    @Override
    public Optional<User> getById(long userId) {
        return userDao.getById(userId);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public Optional<UserAddress> getAddressById(long userId, long addressId) {
        return userDao.getAddressById(userId, addressId);
    }

    @Transactional
    @Override
    public User createOrConsolidate(String email, String password, String name) {
        password = password == null ? null : passwordEncoder.encode(password);

        final Optional<User> maybeUser = userDao.getByEmail(email);
        User user;

        if (!maybeUser.isPresent()) {
            user = userDao.create(email, password, name, LocaleContextHolder.getLocale().getLanguage());
        } else {
            user = maybeUser.get();
            if (password == null) {
                LOGGER.error("Attempted to createOrConsolidate existing user id {} with a null password", user.getUserId());
                throw new IllegalArgumentException("Cannot createOrConsolidate an existing user with a null password");
            }

            if (user.getPassword() != null) {
                LOGGER.error("Attempted to createOrConsolidate an existing and consolidated user id {}", user.getUserId());
                throw new IllegalStateException("Cannot createOrConsolidate an already consolidated user");
            }

            user.setPassword(password);
            user.setName(name);
            LOGGER.info("Consolidated user with id {}", user.getUserId());
        }

        UserVerificationToken token = verificationTokenDao.create(user, generateRandomToken(), generateTokenExpirationDate());

        emailService.sendUserVerificationEmail(user, token.getToken());
        return user;
    }

    @Transactional
    @Override
    public User createIfNotExists(String email, String name) {
        final Optional<User> maybeUser = userDao.getByEmail(email);
        return maybeUser.orElseGet(() -> userDao.create(email, null, name, LocaleContextHolder.getLocale().getLanguage()));
    }

    @Override
    public boolean isUserEmailRegisteredAndConsolidated(String email) {
        final Optional<User> maybeUser = userDao.getByEmail(email);
        return maybeUser.isPresent() && maybeUser.get().getPassword() != null;
    }

    @Transactional
    @Override
    public User updateUser(long userId, String name, String preferredLanguage) {
        final User user = userDao.getById(userId).orElseThrow(UserNotFoundException::new);
        if (name != null)
            user.setName(name);
        if (preferredLanguage != null)
            user.setPreferredLanguage(preferredLanguage);
        return user;
    }

    @Transactional
    @Override
    public UserAddress registerAddress(long userId, String address, String name) {
        return userDao.registerAddress(userId, address, name);
    }

    @Transactional
    @Override
    public UserAddress updateAddress(long userId, long addressId, String address, String name) {
        return userDao.updateAddress(userId, addressId, address, name);
    }

    @Transactional
    @Override
    public void deleteAddress(long userId, long addressId) {
        userDao.deleteAddress(userId, addressId);
    }

    @Transactional
    @Override
    public void resendUserVerificationToken(final String email) {
        final User user = userDao.getByEmail(email).orElse(null);
        if (user == null) {
            LOGGER.info("Ignored resend verification token request for unknown email");
            return;
        }

        if (user.getIsActive()) {
            LOGGER.info("Ignored creating verification token for already active user id {}", user.getUserId());
            return;
        }

        Optional<UserVerificationToken> maybeToken = verificationTokenDao.getByUserId(user.getUserId());
        UserVerificationToken token;
        if (maybeToken.isPresent()) {
            token = maybeToken.get();
            if (token.isExpired()) {
                token.setToken(generateRandomToken());
                token.setExpires(generateTokenExpirationDate());
                LOGGER.info("Refreshed user verification token for user id {}", user.getUserId());
            }
        } else {
            token = verificationTokenDao.create(user, generateRandomToken(), generateTokenExpirationDate());
        }

        emailService.sendUserVerificationEmail(user, token.getToken());
    }

    @Transactional
    @Override
    public void sendPasswordResetToken(final String email) {
        final User user = userDao.getByEmail(email).orElse(null);
        if (user == null) {
            LOGGER.info("Ignored creating password reset token unknown email");
            return;
        }

        if (!user.getIsActive()) {
            LOGGER.info("Ignored creating password reset token for inactive user id {}", user.getUserId());
            return;
        }

        Optional<UserResetpasswordToken> maybeToken = resetpasswordTokenDao.getByUserId(user.getUserId());
        UserResetpasswordToken token;
        if (maybeToken.isPresent()) {
            token = maybeToken.get();
            if (token.isExpired()) {
                token.setToken(generateRandomToken());
                token.setExpires(generateTokenExpirationDate());
                LOGGER.info("Refreshed password reset token for user id {}", user.getUserId());
            }
        } else {
            token = resetpasswordTokenDao.create(user, generateRandomToken(), generateTokenExpirationDate());
        }

        emailService.sendResetPasswordEmail(user, token.getToken());
    }

    @Transactional
    @Override
    public Optional<User> verifyUserAndDeleteVerificationToken(final String token) {
        Optional<UserVerificationToken> maybeToken = verificationTokenDao.getByToken(token);
        if (!maybeToken.isPresent()) {
            LOGGER.info("Ignored verifyAndDeleteVerificationToken call due to token not found");
            return Optional.empty();
        }

        UserVerificationToken uvt = maybeToken.get();
        if (uvt.isExpired()) {
            LOGGER.info("Ignored verifyAndDeleteVerificationToken call for user id {} due to expired token", uvt.getUserId());
            return Optional.empty();
        }

        verificationTokenDao.delete(uvt);

        User user = uvt.getUser();
        if (user.getIsActive()) {
            LOGGER.error("Failed to activate user id {} after successful verification because user is already active", uvt.getUserId());
            return Optional.empty();
        }

        user.setIsActive(true);
        LOGGER.info("Activating user id {} after successful verification", uvt.getUserId());
        return Optional.of(user);
    }

    @Transactional
    @Override
    public boolean updatePasswordAndDeleteResetPasswordToken(String token, String newPassword) {
        if (newPassword == null) {
            LOGGER.info("Attempted to updatePasswordAndDeleteResetPasswordToken with null newPassword");
            throw new IllegalArgumentException("newPassword must not be null");
        }

        Optional<UserResetpasswordToken> maybeToken = resetpasswordTokenDao.getByToken(token);
        if (!maybeToken.isPresent()) {
            LOGGER.info("Ignored updatePasswordAndDeleteResetPasswordToken call due to token not found");
            return false;
        }

        UserResetpasswordToken urt = maybeToken.get();
        if (urt.isExpired()) {
            LOGGER.info("Ignored updatePasswordAndDeleteResetPasswordToken call for user id {} due to expired token", urt.getUserId());
            return false;
        }

        resetpasswordTokenDao.delete(urt);

        User user = urt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        LOGGER.info("Updated password for user id {}", urt.getUserId());
        return true;
    }

    @Override
    public boolean hasActiveVerificationToken(final long userId) {
        Optional<UserVerificationToken> maybeToken = verificationTokenDao.getByUserId(userId);
        return maybeToken.isPresent() && maybeToken.get().isFresh();
    }

    @Override
    public boolean isValidResetPasswordToken(String token) {
        Optional<UserResetpasswordToken> maybeToken = resetpasswordTokenDao.getByToken(token);
        return maybeToken.isPresent() && maybeToken.get().isFresh();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteStaledVerificationTokens() {
        verificationTokenDao.deleteStaledTokens();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deletedStaledResetPasswordTokens() {
        resetpasswordTokenDao.deleteStaledTokens();
    }
}
