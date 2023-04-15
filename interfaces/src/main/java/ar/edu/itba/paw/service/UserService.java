package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserService {
    User create(String username, String password, String name, String email);

    Optional<User> getById(long userId);

    User createIfNotExists(String email, String name);
}
