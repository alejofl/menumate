package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

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
            LOGGER.error("Order Receival Email Sending Failed");
        }
    }

    private long getOrCreateUserId(String name, String email) {
        return userService.createIfNotExists(email, name).getUserId();
    }

    // NOTE: create methods that send emails are not transactional, we want the order to remain placed even if the
    // notification email fails.
    @Override
    public Order createDelivery(long restaurantId, long userId, String address, List<OrderItem> items) {
        Order order = orderDao.createDelivery(restaurantId, userId, address);
        List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items) {
        Order order = orderDao.createDelivery(restaurantId, getOrCreateUserId(name, email), address);
        List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createDineIn(long restaurantId, long userId, int tableNumber, List<OrderItem> items) {
        Order order = orderDao.createDineIn(restaurantId, userId, tableNumber);
        List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items) {
        Order order = orderDao.createDineIn(restaurantId, getOrCreateUserId(name, email), tableNumber);
        List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createTakeAway(long restaurantId, long userId, List<OrderItem> items) {
        Order order = orderDao.createTakeaway(restaurantId, userId);
        List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items) {
        Order order = orderDao.createTakeaway(restaurantId, getOrCreateUserId(name, email));
        List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
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
    public PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize) {
        return orderDao.getByUser(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize, orderStatus);
    }

    @Override
    public Optional<Order> markAsConfirmed(long orderId) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getDateConfirmed() != null || order.getDateCancelled() != null) {
            LOGGER.error("Order {} is already confirmed or cancelled", orderId);
            throw new IllegalStateException("Invalid order status");
        }
        order.setDateConfirmed(LocalDateTime.now());
        try {
            emailService.sendOrderConfirmation(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            LOGGER.error("Order Confirmation Email Sending Failed");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> markAsReady(long orderId) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getDateConfirmed() == null || order.getDateReady() != null || order.getDateCancelled() != null) {
            LOGGER.error("Order {} is not confirmed or is already ready or cancelled", orderId);
            throw new IllegalStateException("Invalid order status");
        }
        order.setDateReady(LocalDateTime.now());
        try {
            emailService.sendOrderReady(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            LOGGER.error("Order Ready Email Sending Failed");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> markAsDelivered(long orderId) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getDateReady() == null || order.getDateDelivered() != null || order.getDateCancelled() != null) {
            LOGGER.error("Order {} is not ready or is already delivered or cancelled", orderId);
            throw new IllegalStateException("Invalid order status");
        }
        order.setDateDelivered(LocalDateTime.now());
        try {
            emailService.sendOrderDelivered(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            LOGGER.error("Order Delivered Email Sending Failed");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> markAsCancelled(long orderId) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getDateCancelled() != null || order.getDateDelivered() != null) {
            LOGGER.error("Order {} is already cancelled or delivered", orderId);
            throw new IllegalStateException("Invalid order status");
        }
        order.setDateCancelled(LocalDateTime.now());
        try {
            emailService.sendOrderCancelled(this.getById(orderId).get());
            return orderDao.getById(orderId);
        } catch (MessagingException e) {
            LOGGER.error("Order Cancelled Email Sending Failed");
            return Optional.empty();
        }
    }

    @Override
    public void setOrderStatus(long orderId, OrderStatus orderStatus) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        switch (orderStatus) {
            case PENDING:
                order.setOrderDates(null, null, null, null);
                LOGGER.info("Order {} set to pending status", orderId);
                break;
            case REJECTED:
                order.setOrderDates(null, null, null, LocalDateTime.now());
                LOGGER.info("Order {} set to rejected status", orderId);
                break;
            case CANCELLED:
                order.setDateDelivered(null);
                order.setDateCancelled(LocalDateTime.now());
                LOGGER.info("Order {} set to cancelled status", orderId);
                break;
            case CONFIRMED:
                order.setOrderDates(LocalDateTime.now(), null, null, null);
                LOGGER.info("Order {} set to confirmed status", orderId);
                break;
            case READY:
                order.setOrderDates(LocalDateTime.now(), LocalDateTime.now(), null, null);
                LOGGER.info("Order {} set to ready status", orderId);
                break;
            case DELIVERED:
                order.setOrderDates(LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null);
                LOGGER.info("Order {} set to delivered status", orderId);
                break;
            default:
                LOGGER.error("Invalid order status");
                throw new IllegalArgumentException("No such OrderType enum constant");
        }
    }

    @Override
    public void updateAddress(long orderId, String address) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getOrderType() != OrderType.DELIVERY) {
            LOGGER.error("Order {} is not a delivery order", orderId);
            throw new IllegalStateException("Invalid order type");
        } else if(order.getDateDelivered() != null || order.getDateCancelled() != null) {
            LOGGER.error("Order {} is already delivered or cancelled", orderId);
            throw new IllegalStateException("Invalid order status");
        }
        order.setAddress(address);
        LOGGER.info("Order {} address updated to {}", orderId, address);
    }

    @Override
    public void updateTableNumber(long orderId, int tableNumber) {
        Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getOrderType() != OrderType.DINE_IN) {
            LOGGER.error("Order {} is not a dine-in order", orderId);
            throw new IllegalStateException("Invalid order type");
        } else if(order.getDateDelivered() != null || order.getDateCancelled() != null) {
            LOGGER.error("Order {} is already delivered or cancelled", orderId);
            throw new IllegalStateException("Invalid order status");
        }
        order.setTableNumber(tableNumber);
        LOGGER.info("Order {} table number updated to {}", orderId, tableNumber);
    }
}
