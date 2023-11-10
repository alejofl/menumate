package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;

import java.util.Optional;

public interface UserService {

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    Optional<UserAddress> getAddressById(long userId, long addressId);

    /**
     * Creates a user, or consolidates it if the user already exists but doesn't have a password, and then sends an
     * email with a verification code.
     *
     * @return The created User
     */
    User createOrConsolidate(String email, String password, String name);

    /**
     * Gets a user by email, or creates it as not-consolidated (no password) if it doesn't exist.
     *
     * @return The created User
     */
    User createIfNotExists(String email, String name);

    boolean isUserEmailRegisteredAndConsolidated(String email);

    User updateUser(long userId, String name, String preferredLanguage);

    UserAddress registerAddress(long userId, String address, String name);

    UserAddress updateAddress(long userId, long addressId, String address, String name);

    void deleteAddress(long userId, long addressId);

    void sendVerificationToken(String email);

    void sendPasswordResetToken(String email);

    Optional<User> verifyUser(String token);

    boolean updatePassword(String token, String newPassword);
}
