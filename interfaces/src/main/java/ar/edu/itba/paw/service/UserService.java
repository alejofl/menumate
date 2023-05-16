package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;

import javax.mail.MessagingException;
import java.util.Optional;

public interface UserService {

    /**
     * Creates a user, or consolidates it if the user already exists but doesn't have a password, and then sends an
     * email with a verification code.
     * @return The created User
     * @throws MessagingException
     */
    User createOrConsolidate(String email, String password, String name) throws MessagingException;

    /**
     * Gets a user by email, or creates it as not-consolidated (no password) if it doesn't exist.
     * @return The created User
     * @throws MessagingException
     */
    User createIfNotExists(String email, String name);

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    boolean isUserEmailRegisteredAndConsolidated(String email);

    Optional<Pair<User, String>> getByEmailWithPassword(String email);
}
