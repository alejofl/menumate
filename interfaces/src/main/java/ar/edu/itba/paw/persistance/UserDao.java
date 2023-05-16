package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;

import java.util.Optional;

public interface UserDao {
    User createOrConsolidate(String email, String password, String name);

    User createIfNotExists(String email, String name);

    void updatePassword(long userId, String password);

    void updateUserActive(long userId, boolean isActive);

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    boolean isUserEmailRegisteredAndConsolidated(String email);

    Optional<Pair<User, String>> getByEmailWithPassword(String email);
}
