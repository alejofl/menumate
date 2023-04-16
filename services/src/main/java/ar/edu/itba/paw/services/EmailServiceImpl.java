package ar.edu.itba.paw.services;

import ar.edu.itba.paw.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine thymeleafTemplateEngine;

    private static final String MENUMATE_EMAIL = "menumate@gmail.com";


    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(to);
        helper.setFrom(MENUMATE_EMAIL);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    public void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Map<String, Object> params) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(params);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }
}
