package ar.edu.itba.paw.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
