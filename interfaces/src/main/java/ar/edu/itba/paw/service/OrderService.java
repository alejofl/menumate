package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order create(long orderTypeId, long restaurantId, long userId);
    Optional<Order> findOrderById(long orderId);
    List<Order> findOrdersByUserId(long userId, long restaurantId);
    List<Order> findOrdersByRestaurantId(long restaurantId);
    List<Order> findByOrdersBetweenDates(LocalDateTime start, LocalDateTime end, long restaurantId);
    List<Order> findByOrdersAddress(String address, long restaurantId);
    List<Order> findByOrdersTableNumber(int tableNumber, long restaurantId);
    List<Order> findByOrdersTypeId(long orderTypeId, long restaurantId);
    boolean updateAddress(long orderId, String address);
    boolean updateTableNumber(long orderId, int tableNumber);
    boolean delete(long orderId);
}
