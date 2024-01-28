package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Service
@PropertySource("classpath:email.properties")
public class EmailServiceImpl implements EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine thymeleafTemplateEngine;

    @Autowired
    private Environment environment;

    @Autowired
    private MessageSource messageSource;

    private void sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setFrom(environment.getProperty("email.address"));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            emailSender.send(message);
            LOGGER.info("Sent email to {} with body length {} and subject {}", to, htmlBody.length(), subject);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email to {} with subject {}", to, subject, e);
        }
    }

    // If a link wants to be passed, set the **path** in params with key "link".
    // This method will concatenate the baseUrl.
    // Only one link should be served for each email.
    private void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Locale locale, Map<String, Object> params) {
        Context thymeleafContext = new Context(locale);
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
    public void sendOrderReceivalForUser(Order order) {
        Locale locale = new Locale(order.getUser().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        params.put("link", "/user/orders/" + order.getOrderId());
        params.put("price", order.getPrice());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_received",
                order.getUser().getEmail(),
                messageSource.getMessage("email.userorderreceived.subject", new Object[]{order.getRestaurant().getName()}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendOrderReceivalForRestaurant(Order order) {
        Restaurant restaurant = order.getRestaurant();
        Locale locale = new Locale(restaurant.getOwner().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("userName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("items", order.getItems());
        params.put("link", String.format("/restaurants/%d/orders", restaurant.getRestaurantId()));
        params.put("price", order.getPrice());
        this.sendMessageUsingThymeleafTemplate(
                "restaurant_order_received",
                restaurant.getEmail(),
                messageSource.getMessage("email.resturantorderreceived.subject", null, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendOrderConfirmation(Order order) {
        Locale locale = new Locale(order.getUser().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/user/orders/" + order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_confirmed",
                order.getUser().getEmail(),
                messageSource.getMessage("email.userorderconfirmed.subject", new Object[]{order.getRestaurant().getName()}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendOrderReady(Order order) {
        Locale locale = new Locale(order.getUser().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/user/orders/" + order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_ready",
                order.getUser().getEmail(),
                messageSource.getMessage("email.userorderready.subject", new Object[]{order.getRestaurant().getName()}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendOrderDelivered(Order order) {
        Locale locale = new Locale(order.getUser().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/user/orders/" + order.getOrderId());
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_delivered",
                order.getUser().getEmail(),
                messageSource.getMessage("email.userorderdelivered.subject", new Object[]{order.getRestaurant().getName()}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendOrderCancelled(Order order) {
        Locale locale = new Locale(order.getUser().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", order.getUser().getName());
        params.put("orderId", order.getOrderId());
        params.put("link", "/restaurants");
        params.put("restaurantName", order.getRestaurant().getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_order_cancelled",
                order.getUser().getEmail(),
                messageSource.getMessage("email.userordercancelled.subject", new Object[]{order.getRestaurant().getName()}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendUserVerificationEmail(User user, String token) {
        Locale locale = new Locale(user.getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        try {
            params.put("link", String.format("/auth/verify?email=%s&token=%s", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.name()), URLEncoder.encode(token, StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        params.put("username", user.getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_verification",
                user.getEmail(),
                messageSource.getMessage("email.userorderverification.subject", null, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendResetPasswordEmail(User user, String token) {
        Locale locale = new Locale(user.getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        try {
            params.put("link", String.format("/auth/reset-password?email=%s&token=%s", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.name()), URLEncoder.encode(token, StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        params.put("username", user.getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_reset_password",
                user.getEmail(),
                messageSource.getMessage("email.userresetpassword.subject", null, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendInvitationToRestaurantStaff(User user, Restaurant restaurant) {
        Locale locale = new Locale(user.getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        try {
            params.put("link", "/auth/register?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        params.put("restaurant", restaurant.getName());
        this.sendMessageUsingThymeleafTemplate(
                "user_restaurant_invitation",
                user.getEmail(),
                messageSource.getMessage("email.userrestaurantinvitation.subject", new Object[]{restaurant.getName()}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendInvitationToUser(User user, String role) {
        Locale locale = new Locale(user.getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        try {
            params.put("link", "/auth/register?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        final String roleLocale = messageSource.getMessage("email.userrole." + role, new Object[]{}, locale);
        this.sendMessageUsingThymeleafTemplate(
                "user_invitation",
                user.getEmail(),
                messageSource.getMessage("email.userroleinvitation.subject", new Object[]{roleLocale}, locale),
                locale,
                params
        );
    }

    @Async
    @Override
    public void sendRestaurantDeactivationEmail(Restaurant restaurant) {
        Locale locale = new Locale(restaurant.getOwner().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", restaurant.getOwner().getName());
        params.put("restaurantName", restaurant.getName());
        this.sendMessageUsingThymeleafTemplate(
                "restaurant_deactivated",
                restaurant.getOwner().getEmail(),
                messageSource.getMessage("email.restaurantdeactivation.subject", new Object[]{restaurant.getName()}, locale),
                locale,
                params
        );
    }

    @Override
    public void sendRestaurantDeletionEmail(Restaurant restaurant) {
        Locale locale = new Locale(restaurant.getOwner().getPreferredLanguage());
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", restaurant.getOwner().getName());
        params.put("restaurantName", restaurant.getName());
        this.sendMessageUsingThymeleafTemplate(
                "restaurant_deleted",
                restaurant.getOwner().getEmail(),
                messageSource.getMessage("email.restaurantdeleted.subject", new Object[]{restaurant.getName()}, locale),
                locale,
                params
        );
    }
}
