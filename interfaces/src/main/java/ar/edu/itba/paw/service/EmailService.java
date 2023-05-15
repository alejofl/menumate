package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;

import javax.mail.MessagingException;

public interface EmailService {
    void sendOrderReceivalForUser(Order order) throws MessagingException;

    void sendOrderReceivalForRestaurant(Restaurant restaurant, Order order) throws MessagingException;

    void sendOrderConfirmation(Order order) throws MessagingException;

    void sendOrderReady(Order order) throws MessagingException;

    void sendOrderDelivered(Order order) throws MessagingException;

    void sendOrderCancelled(Order order) throws MessagingException;

    void sendUserVerificationEmail(String email, String username, String token) throws MessagingException;

    void sendResetPasswordEmail(String email, String username, String token) throws MessagingException;
}
