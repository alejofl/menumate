package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private EmailService emailService;

    private void sendOrderReceivedEmails(Order order) {
        try {
            emailService.sendOrderReceivalForUser(order);
            emailService.sendOrderReceivalForRestaurant(order.getRestaurant(), order);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private long getOrCreateUserId(String name, String email) {
        return userService.createIfNotExists(email, name).getUserId();
    }

    @Override
    public Order createDelivery(long restaurantId, long userId, String address, List<OrderItem> items) {
        Order order = orderDao.createDelivery(restaurantId, userId, address, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items) {
        Order order = orderDao.createDelivery(restaurantId, getOrCreateUserId(name, email), address, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createDineIn(long restaurantId, long userId, int tableNumber, List<OrderItem> items) {
        Order order = orderDao.createDineIn(restaurantId, userId, tableNumber, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items) {
        Order order = orderDao.createDineIn(restaurantId, getOrCreateUserId(name, email), tableNumber, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createTakeAway(long restaurantId, long userId, List<OrderItem> items) {
        Order order = orderDao.createTakeaway(restaurantId, userId, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items) {
        Order order = orderDao.createTakeaway(restaurantId, getOrCreateUserId(name, email), items);
        sendOrderReceivedEmails(order);
        return order;
    }


    @Override
    public OrderItem createOrderItem(long productId, int lineNumber, int quantity, String comment) {
        Product product = productService.getById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product id"));
        return orderDao.createOrderItem(product, lineNumber, quantity, comment);
    }

    @Override
    public Optional<Order> getById(long orderId) {
        return orderDao.getById(orderId);
    }

    @Override
    public Optional<OrderItemless> getByIdExcludeItems(long orderId) {
        return orderDao.getByIdExcludeItems(orderId);
    }

    @Override
    public PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize) {
        return orderDao.getByUser(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<OrderItemless> getByUserExcludeItems(long userId, int pageNumber, int pageSize) {
        return orderDao.getByUserExcludeItems(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<OrderItemless> getInProgressByUserExcludeItems(long userId, int pageNumber, int pageSize) {
        return orderDao.getInProgressByUserExcludeItems(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(long restaurantId, int pageNumber, int pageSize) {
        return orderDao.getByRestaurantExcludeItems(restaurantId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize, orderStatus);
    }

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        return orderDao.getByRestaurantExcludeItems(restaurantId, pageNumber, pageSize, orderStatus);
    }

    @Override
    public Optional<Order> markAsConfirmed(long orderId) {
        try {
            orderDao.markAsConfirmed(orderId);
            emailService.sendOrderConfirmation(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> markAsReady(long orderId) {
        try {
            orderDao.markAsReady(orderId);
            emailService.sendOrderReady(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> markAsDelivered(long orderId) {
        try {
            orderDao.markAsDelivered(orderId);
            emailService.sendOrderDelivered(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> markAsCancelled(long orderId) {
        try {
            orderDao.markAsCancelled(orderId);
            emailService.sendOrderCancelled(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void setOrderStatus(long orderId, OrderStatus orderStatus) {
        orderDao.setOrderStatus(orderId, orderStatus);
    }

    @Override
    public void updateAddress(long orderId, String address) {
        orderDao.updateAddress(orderId, address);
    }

    @Override
    public void updateTableNumber(long orderId, int tableNumber) {
        orderDao.updateTableNumber(orderId, tableNumber);
    }
}
