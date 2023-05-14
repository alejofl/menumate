package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;

import java.util.Optional;

public interface UserDao {
    User create(String email, String password, String name);

    User update(String email, String password, String name);

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    boolean isUserEmailRegistered(String email);

    Optional<Pair<User, String>> getByEmailWithPassword(String email);
}
