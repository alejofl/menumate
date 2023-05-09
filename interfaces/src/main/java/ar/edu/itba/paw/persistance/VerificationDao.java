package ar.edu.itba.paw.persistance;

public interface VerificationDao {

    String generateVerificationToken(final int userId);

    boolean verifyAndDeleteToken(final String token);

    void deleteStaledVerificationTokens();

    boolean hasActiveVerificationToken(final int userId);
}
