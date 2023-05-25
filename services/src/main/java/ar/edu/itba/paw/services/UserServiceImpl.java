package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
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

    @Autowired
    private UserDetailsService userDetailsService;

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

    @Transactional
    @Override
    public User createOrConsolidate(String email, String password, String name) throws MessagingException {
        password = password == null ? null : passwordEncoder.encode(password);

        final Optional<User> maybeUser = userDao.getByEmail(email);
        if (!maybeUser.isPresent())
            return userDao.create(email, password, name, LocaleContextHolder.getLocale().getLanguage());

        final User user = maybeUser.get();
        if (password == null)
            throw new IllegalArgumentException("Cannot createOrConsolidate an existing user with a null password");
        if (user.getPassword() != null)
            throw new IllegalStateException("Cannot createOrConsolidate an already consolidated user");

        user.setPassword(password);
        user.setName(name);

        UserVerificationToken token = verificationTokenDao.create(user, generateRandomToken(), generateTokenExpirationDate());
        emailService.sendUserVerificationEmail(user, token.getToken());
        LOGGER.info("Consolidated user with ID {}", user.getUserId());
        return user;
    }

    @Transactional
    @Override
    public User createIfNotExists(String email, String name) {
        Optional<User> maybeUser = userDao.getByEmail(email);
        if (maybeUser.isPresent())
            return maybeUser.get();

        return userDao.create(email, null, name, LocaleContextHolder.getLocale().getLanguage());
    }

    @Override
    public boolean isUserEmailRegisteredAndConsolidated(String email) {
        Optional<User> maybeUser = userDao.getByEmail(email);
        return maybeUser.isPresent() && maybeUser.get().getPassword() != null;
    }

    @Transactional
    @Override
    public void sendUserVerificationToken(final User user) throws MessagingException {
        if (user.getIsActive())
            return;

        Optional<UserVerificationToken> maybeToken = verificationTokenDao.getByUserId(user.getUserId());
        UserVerificationToken token;
        if (maybeToken.isPresent()) {
            token = maybeToken.get();
            if (!token.getExpires().isAfter(LocalDateTime.now())) {
                token.setToken(generateRandomToken());
                token.setExpires(generateTokenExpirationDate());
            }
        } else {
            token = verificationTokenDao.create(user, generateRandomToken(), generateTokenExpirationDate());
        }

        emailService.sendUserVerificationEmail(user, token.getToken());
    }

    @Transactional
    @Override
    public void sendPasswordResetToken(User user) throws MessagingException {
        if (!user.getIsActive())
            return;

        Optional<UserResetpasswordToken> maybeToken = resetpasswordTokenDao.getByUserId(user.getUserId());
        UserResetpasswordToken token;
        if (maybeToken.isPresent()) {
            token = maybeToken.get();
            if (!token.getExpires().isAfter(LocalDateTime.now())) {
                token.setToken(generateRandomToken());
                token.setExpires(generateTokenExpirationDate());
            }
        } else {
            token = resetpasswordTokenDao.create(user, generateRandomToken(), generateTokenExpirationDate());
        }

        emailService.sendResetPasswordEmail(user, token.getToken());
    }

    @Transactional
    @Override
    public boolean verifyUserAndDeleteVerificationToken(final String token) {
        Optional<UserVerificationToken> maybeToken = verificationTokenDao.getByToken(token);
        if (!maybeToken.isPresent())
            return false;

        UserVerificationToken uvt = maybeToken.get();
        verificationTokenDao.delete(uvt);

        User user = uvt.getUser();
        user.setIsActive(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsService.loadUserByUsername(user.getEmail()), null, AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    @Transactional
    @Override
    public boolean updatePasswordAndDeleteResetPasswordToken(String token, String newPassword) {
        if (newPassword == null)
            throw new IllegalArgumentException("Attempted to updatePasswordAndDeleteResetPasswordToken with null newPassword");

        Optional<UserResetpasswordToken> maybeToken = resetpasswordTokenDao.getByToken(token);
        if (!maybeToken.isPresent())
            return false;

        UserResetpasswordToken urt = maybeToken.get();
        resetpasswordTokenDao.delete(urt);

        User user = urt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    @Override
    public boolean hasActiveVerificationToken(final long userId) {
        Optional<UserVerificationToken> maybeToken = verificationTokenDao.getByUserId(userId);
        return maybeToken.isPresent() && maybeToken.get().getExpires().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isValidResetPasswordToken(String token) {
        Optional<UserResetpasswordToken> maybeToken = resetpasswordTokenDao.getByToken(token);
        return maybeToken.isPresent() && maybeToken.get().getExpires().isAfter(LocalDateTime.now());
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
