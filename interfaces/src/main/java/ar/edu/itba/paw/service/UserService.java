package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;

import javax.mail.MessagingException;
import java.util.Optional;

public interface UserService {

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    /**
     * Creates a user, or consolidates it if the user already exists but doesn't have a password, and then sends an
     * email with a verification code.
     *
     * @return The created User
     * @throws MessagingException When sending the email fails.
     */
    User createOrConsolidate(String email, String password, String name) throws MessagingException;

    /**
     * Gets a user by email, or creates it as not-consolidated (no password) if it doesn't exist.
     *
     * @return The created User
     */
    User createIfNotExists(String email, String name);

    boolean isUserEmailRegisteredAndConsolidated(String email);

    void registerAddress(long userId, String address, String name);

    void deleteAddress(long userId, String address);

    void updateAddress(long userId, String oldAddress, String newAddress);

    void updateAddressName(long userId, String address, String newName);

    Optional<UserAddress> getAddress(long userId, String address);

    void sendUserVerificationToken(User user) throws MessagingException;

    void sendPasswordResetToken(User user) throws MessagingException;

    boolean verifyUserAndDeleteVerificationToken(String token);

    boolean updatePasswordAndDeleteResetPasswordToken(String token, String newPassword);

    boolean hasActiveVerificationToken(long userId);

    boolean isValidResetPasswordToken(String token);
}
