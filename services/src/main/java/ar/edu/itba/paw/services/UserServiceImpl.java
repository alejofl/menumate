package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.TokenService;
import ar.edu.itba.paw.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

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
    public User createOrConsolidate(String email, String password, String name, String language) {
        password = password == null ? null : passwordEncoder.encode(password);

        final Optional<User> maybeUser = userDao.getByEmail(email);
        User user;

        if (!maybeUser.isPresent()) {
            user = userDao.create(email, password, name, language);
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

        final Token token = tokenService.manageUserToken(user);

        emailService.sendUserVerificationEmail(user, token.getToken());
        return user;
    }

    @Transactional
    @Override
    public User createIfNotExists(String email, String name, String language) {
        final Optional<User> maybeUser = userDao.getByEmail(email);
        return maybeUser.orElseGet(() -> userDao.create(email, null, name, language));
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
    public void sendVerificationToken(String email) {
        final User user = userDao.getByEmail(email).orElse(null);
        if (user == null) {
            LOGGER.info("Ignored send verification token request for unknown email");
            return;
        }

        if (user.getIsActive()) {
            LOGGER.info("Ignored send verification token for already active user id {}", user.getUserId());
            return;
        }

        Token token = tokenService.manageUserToken(user);
        emailService.sendUserVerificationEmail(user, token.getToken());
    }

    @Transactional
    @Override
    public void sendPasswordResetToken(String email) {
        final User user = userDao.getByEmail(email).orElse(null);

        if (user == null) {
            LOGGER.info("Ignored sending password reset token request for unknown email");
            return;
        }
        Token token = tokenService.manageUserToken(user);
        emailService.sendResetPasswordEmail(user, token.getToken());
    }

    @Transactional
    @Override
    public Optional<User> verifyUser(String token) {
        Optional<Token> maybeToken = tokenService.getByToken(token);
        if (!maybeToken.isPresent()) {
            LOGGER.info("Ignored verifyUser call due to token not found or stale");
            return Optional.empty();
        }

        final Token tkn = maybeToken.get();
        final User user = tkn.getUser();

        tokenService.delete(tkn);

        if (user.getIsActive()) {
            LOGGER.error("Failed to activate user id {} after successful verification because user is already active", user.getUserId());
            return Optional.empty();
        }

        user.setIsActive(true);
        LOGGER.info("Activating user id {} after successful verification", user.getUserId());
        return Optional.of(user);
    }

    @Transactional
    @Override
    public boolean updatePassword(long userId, String newPassword) {
        if (newPassword == null) {
            LOGGER.info("Attempted to update password with null newPassword");
            throw new IllegalArgumentException("newPassword must not be null");
        }

        Optional<User> maybeUser = userDao.getById(userId);
        if (!maybeUser.isPresent()) {
            LOGGER.info("Ignored updating password due to unknown email");
            return false;
        }
        User user = maybeUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        LOGGER.info("Updated password for user id {}", user.getUserId());
        return true;
    }

}
