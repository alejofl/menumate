package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.InvalidOrderTypeException;
import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    private void sendOrderReceivedEmails(Order order) {
        emailService.sendOrderReceivalForUser(order);
        emailService.sendOrderReceivalForRestaurant(order);
    }

    private void assingOrderItemsToOrder(Order order, List<OrderItem> items) {
        for (OrderItem item : items)
            item.setOrderId(order.getOrderId());
        final List<OrderItem> orderList = order.getItems();
        orderList.addAll(items);
    }

    private Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items) {
        final User user = userService.createIfNotExists(email, name);
        final Order order = orderDao.createDelivery(restaurantId, user.getUserId(), address);
        userDao.refreshAddress(user.getUserId(), address);
        assingOrderItemsToOrder(order, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    private Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items) {
        final User user = userService.createIfNotExists(email, name);
        final Order order = orderDao.createDineIn(restaurantId, user.getUserId(), tableNumber);
        assingOrderItemsToOrder(order, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    private Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items) {
        final User user = userService.createIfNotExists(email, name);
        final Order order = orderDao.createTakeaway(restaurantId, user.getUserId());
        assingOrderItemsToOrder(order, items);
        sendOrderReceivedEmails(order);
        return order;
    }

    @Override
    public OrderItem createOrderItem(long restaurantId, long productId, int lineNumber, int quantity, String comment) {
        return orderDao.createOrderItem(restaurantId, productId, lineNumber, quantity, comment);
    }

    @Transactional
    @Override
    public Order create(OrderType orderType, Long restaurantId, String name, String email, Integer tableNumber, String address, List<OrderItem> items) {
        Order order;
        if (orderType == OrderType.DINE_IN) {
            order = createDineIn(restaurantId, name, email, tableNumber, items);
        } else if (orderType == OrderType.TAKEAWAY) {
            order = createTakeAway(restaurantId, name, email, items);
        } else if (orderType == OrderType.DELIVERY) {
            order = createDelivery(restaurantId, name, email, address, items);
        } else {
            throw new InvalidOrderTypeException("Order type not supported");
        }
        return order;
    }

    @Override
    public Optional<Order> getById(long orderId) {
        return orderDao.getById(orderId);
    }

    @Override
    public PaginatedResult<Order> get(Long userId, Long restaurantId, OrderStatus orderStatus, boolean onlyInProgress, boolean descending, int pageNumber, int pageSize) {
        return orderDao.get(userId, restaurantId, orderStatus, onlyInProgress, descending, pageNumber, pageSize);
    }

    @Transactional
    @Override
    public Order advanceOrderStatus(long orderId, OrderStatus newStatus) {
        final Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        final OrderStatus status = order.getOrderStatus();
        if (!status.isInProgress()) {
            LOGGER.warn("Attempted to advance the status of order id {}, but order is {}", orderId, status.getMessageCode());
            throw new InvalidUserArgumentException("Cannot update the status of a finished order");
        }

        switch (newStatus) {
            case CONFIRMED:
                if (status != OrderStatus.PENDING)
                    throw new InvalidUserArgumentException("Only orders whose status is PENDING may be marked as CONFIRMED");
                order.setDateConfirmed(LocalDateTime.now());
                emailService.sendOrderConfirmation(order);
                break;

            case READY:
                if (status != OrderStatus.CONFIRMED)
                    throw new InvalidUserArgumentException("Only orders whose status is CONFIRMED may be marked as READY");
                order.setDateReady(LocalDateTime.now());
                emailService.sendOrderReady(order);
                break;

            case DELIVERED:
                if (status != OrderStatus.READY)
                    throw new InvalidUserArgumentException("Only orders whose status is READY may be marked as DELIVERED");
                order.setDateDelivered(LocalDateTime.now());
                emailService.sendOrderDelivered(order);
                break;

            case REJECTED:
                if (order.getDateConfirmed() != null)
                    throw new InvalidUserArgumentException("Cannot reject an order that has been confirmed");
            case CANCELLED:
                order.setDateCancelled(LocalDateTime.now());
                emailService.sendOrderCancelled(order);
                break;

            default:
                LOGGER.warn("Attempted to advance order id {} status from {} to {}", orderId, status.getMessageCode(), newStatus.getMessageCode());
                throw new InvalidUserArgumentException(String.format("Cannot set the status of an order from %s to %s", status.getMessageCode(), newStatus.getMessageCode()));
        }

        LOGGER.info("Updated status of order id {} to {}", order.getOrderId(), order.getOrderStatus());
        return order;
    }

    @Override
    public Optional<List<OrderItem>> getOrderItemsById(long orderId) {
        return orderDao.getOrderItemsById(orderId);
    }

    @Transactional
    @Override
    public void setOrderStatus(long orderId, OrderStatus orderStatus) {
        final Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        final LocalDateTime now = LocalDateTime.now();
        switch (orderStatus) {
            case PENDING:
                order.setDateConfirmed(null);
                order.setDateReady(null);
                order.setDateDelivered(null);
                order.setDateCancelled(null);
                break;
            case REJECTED:
                order.setDateConfirmed(null);
                order.setDateReady(null);
                order.setDateDelivered(null);
                order.setDateCancelled(Utils.coalesce(order.getDateCancelled(), now));
                break;
            case CANCELLED:
                order.setDateDelivered(null);
                order.setDateCancelled(Utils.coalesce(order.getDateCancelled(), now));
                break;
            case CONFIRMED:
                order.setDateConfirmed(Utils.coalesce(order.getDateConfirmed(), now));
                order.setDateReady(null);
                order.setDateDelivered(null);
                order.setDateCancelled(null);
                break;
            case READY:
                order.setDateConfirmed(Utils.coalesce(order.getDateConfirmed(), now));
                order.setDateReady(Utils.coalesce(order.getDateReady(), now));
                order.setDateDelivered(null);
                order.setDateCancelled(null);
                break;
            case DELIVERED:
                order.setDateConfirmed(Utils.coalesce(order.getDateConfirmed(), now));
                order.setDateReady(Utils.coalesce(order.getDateReady(), now));
                order.setDateDelivered(Utils.coalesce(order.getDateDelivered(), now));
                order.setDateCancelled(null);
                break;
            default:
                LOGGER.error("Attempted to force set order status to unknown OrderStatus value: {}", orderStatus);
                throw new IllegalArgumentException("No such OrderType enum constant");
        }

        LOGGER.info("Forced set order {} set to status {}", orderId, orderStatus);
    }

    @Transactional
    @Override
    public void updateAddress(long orderId, String address) {
        final Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getOrderType() != OrderType.DELIVERY) {
            LOGGER.error("Attempted to update address of non-delivery order {}", orderId);
            throw new IllegalStateException("Invalid order type");
        } else if (!order.getOrderStatus().isInProgress()) {
            LOGGER.error("Attempted to update address of closed order {}", orderId);
            throw new IllegalStateException("Invalid order status");
        }

        address = address == null ? null : address.trim();
        if (address == null || address.isEmpty()) {
            LOGGER.error("Attempted to update address of order {} to null-or-blank value", orderId);
            throw new IllegalArgumentException("Cannot set order address to null");
        }

        order.setAddress(address.trim());
        LOGGER.info("Order {} address updated", orderId);
    }

    @Transactional
    @Override
    public void updateTableNumber(long orderId, int tableNumber) {
        final Order order = orderDao.getById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getOrderType() != OrderType.DINE_IN) {
            LOGGER.error("Attempted to update tablenum of non-dinein order {}", orderId);
            throw new IllegalStateException("Invalid order type");
        } else if (!order.getOrderStatus().isInProgress()) {
            LOGGER.error("Attempted to update tablenum of closed order {}", orderId);
            throw new IllegalStateException("Invalid order status");
        }

        order.setTableNumber(tableNumber);
        LOGGER.info("Order {} table number updated to {}", orderId, tableNumber);
    }
}
