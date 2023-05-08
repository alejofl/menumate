package ar.edu.itba.paw.persistance;

public interface VerificationDao {

    String generateVerificationToken(String email);

    boolean verifyAndDeleteToken(String email, String token);

    void deleteStaledVerificationTokens();

    boolean hasActiveVerificationToken(String email);
}
