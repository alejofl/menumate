package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.UserService;
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

    private int getOrCreateUserId(String name, String email) {
        return userService.createIfNotExists(email, name).getUserId();
    }

    @Override
    public Order createDelivery(int restaurantId, int userId, String address, List<OrderItem> items) {
        return orderDao.createDelivery(restaurantId, userId, address, items);
    }

    @Override
    public Order createDelivery(int restaurantId, String name, String email, String address, List<OrderItem> items) {
        return orderDao.createDelivery(restaurantId, getOrCreateUserId(name, email), address, items);
    }

    @Override
    public Order createDineIn(int restaurantId, int userId, int tableNumber, List<OrderItem> items) {
        return orderDao.createDineIn(restaurantId, userId, tableNumber, items);
    }

    @Override
    public Order createDineIn(int restaurantId, String name, String email, int tableNumber, List<OrderItem> items) {
        return orderDao.createDineIn(restaurantId, getOrCreateUserId(name, email), tableNumber, items);
    }

    @Override
    public Order createTakeAway(int restaurantId, int userId, List<OrderItem> items) {
        return orderDao.createTakeaway(restaurantId, userId, items);
    }

    @Override
    public Order createTakeAway(int restaurantId, String name, String email, List<OrderItem> items) {
        return orderDao.createTakeaway(restaurantId, getOrCreateUserId(name, email), items);
    }


    @Override
    public OrderItem createOrderItem(int productId, int lineNumber, int quantity, String comment) {
        Product product = productService.getById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product id"));
        return orderDao.createOrderItem(product, lineNumber, quantity, comment);
    }

    @Override
    public Optional<Order> getById(int orderId) {
        return orderDao.getById(orderId);
    }

    @Override
    public Optional<OrderItemless> getByIdExcludeItems(int orderId) {
        return orderDao.getByIdExcludeItems(orderId);
    }

    @Override
    public PaginatedResult<Order> getByUser(int userId, int pageNumber, int pageSize) {
        return orderDao.getByUser(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<OrderItemless> getByUserExcludeItems(int userId, int pageNumber, int pageSize) {
        return orderDao.getByUserExcludeItems(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<OrderItemless> getInProgressByUserExcludeItems(int userId, int pageNumber, int pageSize) {
        return orderDao.getInProgressByUserExcludeItems(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(int restaurantId, int pageNumber, int pageSize) {
        return orderDao.getByRestaurantExcludeItems(restaurantId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize, orderStatus);
    }

    @Override
    public PaginatedResult<OrderItemless> getByRestaurantExcludeItems(int restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        return orderDao.getByRestaurantExcludeItems(restaurantId, pageNumber, pageSize, orderStatus);
    }

    @Override
    public boolean markAsConfirmed(int orderId) {
        boolean success = orderDao.markAsConfirmed(orderId);
        if (success) {
            try {
                emailService.sendOrderConfirmation(this.getById(orderId).get());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public boolean markAsReady(int orderId) {
        boolean success = orderDao.markAsReady(orderId);
        if (success) {
            try {
                emailService.sendOrderReady(this.getById(orderId).get());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public boolean markAsDelivered(int orderId) {
        boolean success = orderDao.markAsDelivered(orderId);
        if (success) {
            try {
                emailService.sendOrderDelivered(this.getById(orderId).get());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public boolean markAsCancelled(int orderId) {
        boolean success = orderDao.markAsCancelled(orderId);
        if (success) {
            try {
                emailService.sendOrderCancelled(this.getById(orderId).get());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public boolean setOrderStatus(int orderId, OrderStatus orderStatus) {
        return orderDao.setOrderStatus(orderId, orderStatus);
    }

    @Override
    public boolean updateAddress(int orderId, String address) {
        return orderDao.updateAddress(orderId, address);
    }

    @Override
    public boolean updateTableNumber(int orderId, int tableNumber) {
        return orderDao.updateTableNumber(orderId, tableNumber);
    }

    @Override
    public boolean delete(int orderId) {
        return orderDao.delete(orderId);
    }

}
