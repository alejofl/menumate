package ar.edu.itba.paw.service;

public interface VerificationService {

    String generateVerificationToken(final String email);

    boolean verifyAndDeleteToken(final String token);

    public boolean hasActiveVerificationToken(final String email);
}
