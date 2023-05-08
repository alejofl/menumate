package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User create(String email, String password, String name) {
        // TODO: send email validation mail
        if (isUserEmailRegistered(email)) {
            throw new IllegalStateException();
        }
        if (password == null) {
            return userDao.create(email, null, name);
        } else {
            if (getByEmail(email).isPresent()) {
                return userDao.update(email, passwordEncoder.encode(password), name);
            }
            return userDao.create(email, passwordEncoder.encode(password), name);
        }
    }

    @Override
    public Optional<User> getById(int userId) {
        return userDao.getById(userId);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public boolean isUserEmailRegistered(String email) {
        return userDao.isUserEmailRegistered(email);
    }

    @Override
    public Optional<Pair<User, String>> getByEmailWithPassword(String email) {
        return userDao.getByEmailWithPassword(email);
    }

    @Override
    public User createIfNotExists(String email, String name) {
        Optional<User> user = this.getByEmail(email);
        return user.orElseGet(() -> this.create(email, null, name));
    }

    @Override
    public boolean verifyAccount(String email) {
        return userDao.verifyAccount(email);
    }

}
