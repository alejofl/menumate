package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createDelivery(int restaurantId, int userId, String address, List<OrderItem> items);

    Order createDelivery(int restaurantId, String name, String email, String address, List<OrderItem> items);

    Order createDineIn(int restaurantId, int userId, int tableNumber, List<OrderItem> items);

    Order createDineIn(int restaurantId, String name, String email, int tableNumber, List<OrderItem> items);

    Order createTakeAway(int restaurantId, int userId, List<OrderItem> items);

    Order createTakeAway(int restaurantId, String name, String email, List<OrderItem> items);

    OrderItem createOrderItem(int productId, int i, int quantity, String comment);

    Optional<Order> getById(int orderId);

    PaginatedResult<Order> getByUser(int userId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus);

    boolean markAsConfirmed(int orderId);

    boolean markAsReady(int orderId);

    boolean markAsDelivered(int orderId);

    boolean markAsCancelled(int orderId);

    boolean updateAddress(int orderId, String address);

    boolean updateTableNumber(int orderId, int tableNumber);

    boolean delete(int orderId);
}
