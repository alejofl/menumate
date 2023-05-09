package ar.edu.itba.paw.persistance;

public interface VerificationDao {

    String generateVerificationToken(final String email);

    boolean verifyAndDeleteToken(final String token);

    void deleteStaledVerificationTokens();

    boolean hasActiveVerificationToken(final String email);
}
