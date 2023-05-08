package ar.edu.itba.paw.service;

public interface VerificationService {

    String generateVerificationToken(String email);

    boolean verificationTokenIsValid(String email, String token);

    boolean deleteVerificationToken(String email);

    void deleteStaledVerificationTokens();

    boolean verificationTokenIsStaled(String email);
}
