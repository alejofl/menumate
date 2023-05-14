package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
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
@PropertySource("classpath:email.properties")
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine thymeleafTemplateEngine;

    @Autowired
    private Environment environment;

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(to);
        helper.setFrom(environment.getProperty("email.address"));
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    // If a link wants to be passed, set the **path** in params with key "link".
    // This method will concatenate the baseUrl.
    // Only one link should be served for each email.
    private void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Map<String, Object> params) throws MessagingException {
        Context thymeleafContext = new Context(LocaleContextHolder.getLocale());
        final String baseUrl = environment.getProperty("email.base_url");
        if (params.containsKey("link")) {
            params.put("link", baseUrl + params.get("link"));
        }
        thymeleafContext.setVariables(params);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }

    @Async
    @Override
    public void sendOrderReceivalForUser(Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        params.put("link", "/orders/" + order.getOrderId());
        params.put("price", order.getPrice());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_received",
                order.getUser().getEmail(),
                String.format("MenuMate - %s received your order!", order.getRestaurant().getName()),
                params
        );
    }

    @Async
    @Override
    public void sendOrderReceivalForRestaurant(Restaurant restaurant, Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("userName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("items", order.getItems());
        params.put("link", String.format("/restaurants/%d/orders", restaurant.getRestaurantId()));
        params.put("price", order.getPrice());
        this.sendMessageUsingThymeleafTemplate(
                "restaurant_order_received",
                restaurant.getEmail(),
                "MenuMate - New order received.",
                params
        );
    }

    @Async
    @Override
    public void sendOrderConfirmation(Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/orders/" + order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_confirmed",
                order.getUser().getEmail(),
                String.format("MenuMate - %s confirmed your order!", order.getRestaurant().getName()),
                params
        );
    }

    @Async
    @Override
    public void sendOrderReady(Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/orders/" + order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_ready",
                order.getUser().getEmail(),
                String.format("MenuMate - %s has your order ready!", order.getRestaurant().getName()),
                params
        );
    }

    @Async
    @Override
    public void sendOrderDelivered(Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/orders/" + order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_delivered",
                order.getUser().getEmail(),
                String.format("MenuMate - Your order from %s has been delivered.", order.getRestaurant().getName()),
                params
        );
    }

    @Async
    @Override
    public void sendOrderCancelled(Order order) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/restaurants");
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_cancelled",
                order.getUser().getEmail(),
                String.format("MenuMate - Your order from %s has been cancelled.", order.getRestaurant().getName()),
                params
        );
    }

    @Async
    @Override
    public void sendUserVerificationEmail(String email, String username, String token) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("link", "/auth/verify?token=" + token);
        params.put("username", username);
        this.sendMessageUsingThymeleafTemplate(
                "user_verification",
                email,
                "MenuMate - Verify your account",
                params
        );
    }

    @Async
    @Override
    public void sendResetPasswordEmail(String email, String username, String token) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        params.put("link", "/auth/reset-password?token=" + token);
        params.put("username", username);
        this.sendMessageUsingThymeleafTemplate(
                "user_reset_password",
                email,
                "MenuMate - Reset your password",
                params
        );
    }
}
