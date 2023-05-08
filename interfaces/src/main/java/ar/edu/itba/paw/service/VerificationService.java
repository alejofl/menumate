package ar.edu.itba.paw.service;

public interface VerificationService {

    String generateVerificationToken(String email);

    boolean verifyAndDeleteToken(String email, String token);

    boolean hasActiveVerificationToken(String email);
}
