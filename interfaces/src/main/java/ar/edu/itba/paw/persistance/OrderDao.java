package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Order create(long orderTypeId, long restaurantId, long userId);

    Optional<Order> getById(long orderId);

    List<Order> getByUser(long userId, long restaurantId);

    List<Order> getByOrderTypeAndRestaurant(long orderTypeId, long restaurantId);

    List<Order> getByRestaurant(long restaurantId);

    List<Order> getByRestaurantBetweenDates(long restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByRestaurantAndAddress(long restaurantId, String address);

    List<Order> getByRestaurantAndTableNumber(long restaurantId, int tableNumber);

    boolean updateAddress(long orderId, String address);

    boolean updateTableNumber(long orderId, int tableNumber);

    boolean delete(long orderId);
}
