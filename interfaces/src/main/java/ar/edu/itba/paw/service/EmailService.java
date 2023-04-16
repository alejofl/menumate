package ar.edu.itba.paw.service;

import javax.mail.MessagingException;
import java.util.Map;

public interface EmailService {

    void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Map<String, Object> params) throws MessagingException;
}
