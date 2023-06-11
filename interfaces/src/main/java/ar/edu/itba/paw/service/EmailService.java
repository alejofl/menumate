package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.User;

public interface EmailService {
    void sendOrderReceivalForUser(Order order);

    void sendOrderReceivalForRestaurant(Order order);

    void sendOrderConfirmation(Order order);

    void sendOrderReady(Order order);

    void sendOrderDelivered(Order order);

    void sendOrderCancelled(Order order);

    void sendUserVerificationEmail(User user, String token);

    void sendResetPasswordEmail(User user, String token);

    void sendInvitationToRestaurantStaff(User user, Restaurant restaurant);
}
