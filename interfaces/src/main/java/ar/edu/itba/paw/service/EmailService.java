package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.User;

import javax.mail.MessagingException;

public interface EmailService {

    void sendUserOrderConfirmation(User user, Order order) throws MessagingException;

    void sendRestaurantOrderConfirmation(Restaurant restaurant, Order order) throws MessagingException;
}
