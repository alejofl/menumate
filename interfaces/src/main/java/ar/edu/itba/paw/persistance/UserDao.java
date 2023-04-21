package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;

import java.util.Optional;

public interface UserDao {
    User create(String email, String password, String name);

    Optional<User> getById(int userId);

    Optional<Pair<User, String>> getByEmailWithPassword(String email);

    Optional<User> getByEmail(String email);
}
