package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.User;

import javax.mail.MessagingException;
import java.util.Optional;

public interface UserService {
    User create(String username, String password, String name, String email);

    Optional<User> getByEmail(String email);

    Optional<User> getById(int userId);

    User createIfNotExists(String email, String name);
}
