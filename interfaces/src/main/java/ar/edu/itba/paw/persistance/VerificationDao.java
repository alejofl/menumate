package ar.edu.itba.paw.persistance;

public interface VerificationDao {

    String generateVerificationToken(String email);

    boolean verificationTokenIsValid(String email, String token);

    void deleteStaledVerificationTokens();

    boolean deleteVerificationToken(String email);
}
