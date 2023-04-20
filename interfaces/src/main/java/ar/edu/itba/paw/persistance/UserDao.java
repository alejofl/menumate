package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {
    User create(String username, String password, String name, String email);

    Optional<User> getById(int userId);

    Optional<User> getByEmailAndPassword(String email, String password);

    Optional<User> getByEmail(String email);
}
