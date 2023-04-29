package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;

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

    List<Order> getByUser(int userId);

    List<Order> getByRestaurant(int restaurantId);

    List<Order> getOrderedBetweenDates(int restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByAddress(int restaurantId, String address);

    List<Order> getByTableNumber(int restaurantId, int tableNumber);

    List<Order> getByOrderTypeAndRestaurant(OrderType orderType, int restaurantId);

    boolean updateAddress(int orderId, String address);

    boolean updateTableNumber(int orderId, int tableNumber);

    boolean delete(int orderId);
}
