package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.VerificationTokenDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenDao verificationTokenDao;

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

        userDao.updatePassword(user, password);
        LOGGER.info("Consolidated user with ID {}", user.getUserId());

        String token = verificationTokenDao.generateToken(user.getUserId());
        emailService.sendUserVerificationEmail(user, token);
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
}
