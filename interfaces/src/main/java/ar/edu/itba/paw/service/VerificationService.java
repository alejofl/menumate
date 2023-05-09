package ar.edu.itba.paw.service;

public interface VerificationService {

    String generateVerificationToken(final int userId);

    boolean verifyAndDeleteToken(final String token);

    public boolean hasActiveVerificationToken(final int userId);
}
