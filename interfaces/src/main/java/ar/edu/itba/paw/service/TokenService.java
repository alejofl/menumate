package ar.edu.itba.paw.service;

public interface TokenService {

    String generateVerificationToken(final int userId);

    String generatePasswordResetToken(final int userId);

    boolean verifyUserAndDeleteVerificationToken(final String token);

    boolean updatePasswordAndDeleteResetPasswordToken(final String token, final String newPassword);

    boolean hasActiveVerificationToken(final int userId);

    boolean hasActiveResetPasswordToken(final int userId);

    boolean isValidVerificationToken(final String token);

    boolean isValidResetPasswordToken(final String token);
}