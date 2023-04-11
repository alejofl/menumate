package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> getUserById(long id);

    User create(String username, String password, String email);
}
