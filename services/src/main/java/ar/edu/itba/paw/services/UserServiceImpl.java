package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.VerificationTokenDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @Autowired
    private EmailService emailService;

    @Transactional
    @Override
    public User createOrConsolidate(String email, String password, String name) throws MessagingException {
        password = password == null ? null : passwordEncoder.encode(password);
        final User user = userDao.createOrConsolidate(email, password, name);
        String token = verificationTokenDao.generateToken(user.getUserId());
        emailService.sendUserVerificationEmail(user.getEmail(), user.getName(), token);
        return  user;
    }

    @Override
    public User createIfNotExists(String email, String name) {
        return userDao.createIfNotExists(email, name);
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
    public boolean isUserEmailRegisteredAndConsolidated(String email) {
        return userDao.isUserEmailRegisteredAndConsolidated(email);
    }

    @Override
    public Optional<Pair<User, String>> getByEmailWithPassword(String email) {
        return userDao.getByEmailWithPassword(email);
    }
}
