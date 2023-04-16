package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    @Override
    public User create(String username, String password, String name, String email) {
        // TODO: validate username/ password
        // TODO: send email validation mail
        return userDao.create(username, password, name, email);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public Optional<User> getById(long userId) {
        return userDao.getById(userId);
    }

    @Override
    public User createIfNotExists(String email, String name) {
        Optional<User> user = this.getByEmail(email);
        return user.orElseGet(() -> this.create(null, null, name, email));
    }

    @Override
    public void sendOrderConfirmation(User user, Order order) throws MessagingException{
        final Map<String, Object> params = new HashMap<>();
        params.put("recipientName", user.getName());
        params.put("orderId", order.getOrderId());
        params.put("senderName", order.getRestaurant().getName());
        emailService.sendMessageUsingThymeleafTemplate("template-thymeleaf", user.getEmail(), "Order confirmation", params);
    }
}
