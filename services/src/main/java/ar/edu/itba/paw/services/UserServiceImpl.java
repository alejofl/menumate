package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> getById(long userId) {
        return userDao.getById(userId);
    }

    @Override
    public User create(String username, String password, String email) {
        // TODO: validate username/ password
        // TODO: send email validation mail
        return userDao.create(username, password, email);
    }
}
