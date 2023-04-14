package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order create(long orderTypeId, long restaurantId, long userId);

    Optional<Order> getById(long orderId);

    List<Order> getByUser(long userId, long restaurantId);

    List<Order> getByRestaurant(long restaurantId);

    List<Order> getBetweenDates(long restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByAddress(long restaurantId, String address);

    List<Order> getByTableNumber(long restaurantId, int tableNumber);

    List<Order> getByOrderType(long orderTypeId, long restaurantId);

    boolean updateAddress(long orderId, String address);

    boolean updateTableNumber(long orderId, int tableNumber);

    boolean delete(long orderId);
}
