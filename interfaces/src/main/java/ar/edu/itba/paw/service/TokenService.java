package ar.edu.itba.paw.service;

public interface TokenService {

    String generateVerificationToken(final int userId);

    String generatePasswordResetToken(final int userId);

    boolean verifyAndDeleteVerificationToken(final String token);

    boolean verifyAndDeletePasswordResetToken(final String token);

    boolean hasActiveVerificationToken(final int userId);

    boolean hasActiveResetPasswordToken(final int userId);
}
