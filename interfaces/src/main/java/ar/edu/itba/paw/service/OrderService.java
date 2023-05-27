package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createDelivery(long restaurantId, long userId, String address, List<OrderItem> items);

    Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items);

    Order createDineIn(long restaurantId, long userId, int tableNumber, List<OrderItem> items);

    Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items);

    Order createTakeAway(long restaurantId, long userId, List<OrderItem> items);

    Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items);

    OrderItem createOrderItem(long productId, int i, int quantity, String comment);

    Optional<Order> getById(long orderId);

    PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus);

    Optional<Order> markAsConfirmed(long orderId);

    Optional<Order> markAsReady(long orderId);

    Optional<Order> markAsDelivered(long orderId);

    Optional<Order> markAsCancelled(long orderId);

    /**
     * Warning: this method forcedly modifies an order's status. No checks are performed and no
     * notifications are sent, so this method should only be used on extreme circumstances.
     */
    void setOrderStatus(long orderId, OrderStatus orderStatus);

    void updateAddress(long orderId, String address);

    void updateTableNumber(long orderId, int tableNumber);
}
