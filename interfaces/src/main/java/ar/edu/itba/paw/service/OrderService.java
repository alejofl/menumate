package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderItemless;
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

    Optional<OrderItemless> getByIdExcludeItems(long orderId);

    PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize);

    PaginatedResult<OrderItemless> getByUserExcludeItems(long userId, int pageNumber, int pageSize);

    PaginatedResult<OrderItemless> getInProgressByUserExcludeItems(long userId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize);

    PaginatedResult<OrderItemless> getByRestaurantExcludeItems(long restaurantId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus);

    PaginatedResult<OrderItemless> getByRestaurantExcludeItems(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus);

    boolean markAsConfirmed(long orderId);

    boolean markAsReady(long orderId);

    boolean markAsDelivered(long orderId);

    boolean markAsCancelled(long orderId);

    boolean setOrderStatus(long orderId, OrderStatus orderStatus);

    boolean updateAddress(long orderId, String address);

    boolean updateTableNumber(long orderId, int tableNumber);

    boolean delete(long orderId);
}
