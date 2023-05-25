package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;

import java.util.Optional;

public interface UserDao {

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    User create(String email, String password, String name, String language);

    void updatePassword(User user, String password);

    void updateIsActive(User user, boolean isActive);
}
