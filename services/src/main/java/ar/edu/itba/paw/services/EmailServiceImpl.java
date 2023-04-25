package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private RestaurantService restaurantService;

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

    private void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Map<String, Object> params) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(params);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }

    @Async
    public void sendUserOrderConfirmation(Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        params.put("price", restaurantService.getOrderPrice(order));
        this.sendMessageUsingThymeleafTemplate(
                "user_order_confirmation",
                order.getUser().getEmail(),
                String.format("MenuMate - %s confirmed your order!", order.getRestaurant().getName()),
                params
        );
    }

    @Async
    public void sendRestaurantOrderConfirmation(Restaurant restaurant, Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("userName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("items", order.getItems());
        params.put("price", restaurantService.getOrderPrice(order));
        this.sendMessageUsingThymeleafTemplate(
                "restaurant_order_confirmation",
                restaurant.getEmail(),
                "MenuMate - New order received.",
                params
        );
    }
}
