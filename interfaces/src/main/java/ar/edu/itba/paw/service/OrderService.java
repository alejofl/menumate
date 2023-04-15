package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createDelivery(long restaurantId, long userId, String address, List<OrderItem> items);

    Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items);

    Order createDineIn(long restaurantId, long userId, int tableNumber, List<OrderItem> items);

    Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items);

    Order createTakeAway(long restaurantId, long userId, List<OrderItem> items);

    Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items);

    Optional<Order> getById(long orderId);

    List<Order> getByUser(long userId, long restaurantId);

    List<Order> getByRestaurant(long restaurantId);

    List<Order> getOrderedBetweenDates(long restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByAddress(long restaurantId, String address);

    List<Order> getByTableNumber(long restaurantId, int tableNumber);

    List<Order> getByOrderTypeAndRestaurant(OrderType orderType, long restaurantId);

    boolean updateAddress(long orderId, String address);

    boolean updateTableNumber(long orderId, int tableNumber);

    boolean delete(long orderId);
}
