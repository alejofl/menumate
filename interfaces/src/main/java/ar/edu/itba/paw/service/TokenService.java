package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;

import javax.mail.MessagingException;

public interface TokenService {
    /**
     * Sends a verification token by email to the given user, if it isn't active.
     * @throws MessagingException
     */
    void sendUserVerificationToken(final User user) throws MessagingException;

    /**
     * Sends a password reset token by email to the given user, if it is active.
     * @throws MessagingException
     */
    void sendPasswordResetToken(final User user) throws MessagingException;

    boolean verifyUserAndDeleteVerificationToken(final String token);

    boolean updatePasswordAndDeleteResetPasswordToken(final String token, final String newPassword);

    boolean hasActiveVerificationToken(final long userId);

    boolean isValidResetPasswordToken(final String token);
}
