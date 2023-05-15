package ar.edu.itba.paw.service;

import javax.mail.MessagingException;

public interface TokenService {
    String generateVerificationToken(final long userId) throws MessagingException;

    String generatePasswordResetToken(final long userId) throws MessagingException;

    boolean verifyUserAndDeleteVerificationToken(final String token);

    boolean updatePasswordAndDeleteResetPasswordToken(final String token, final String newPassword);

    boolean hasActiveVerificationToken(final long userId);

    boolean isValidResetPasswordToken(final String token);
}
