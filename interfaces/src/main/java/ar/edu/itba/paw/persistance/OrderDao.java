package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> getById(long orderId);

    List<Order> getByUser(long userId, long restaurantId);

    List<Order> getByOrderTypeAndRestaurant(OrderType orderType, long restaurantId);

    List<Order> getByRestaurant(long restaurantId);

    List<Order> getByRestaurantOrderedBetweenDates(long restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByRestaurantAndAddress(long restaurantId, String address);

    List<Order> getByRestaurantAndTableNumber(long restaurantId, int tableNumber);

    boolean updateAddress(long orderId, String address);

    boolean updateTableNumber(long orderId, int tableNumber);

    boolean delete(long orderId);

    Order create(OrderType orderType, long restaurantId, long userId, String address, List<OrderItem> items);
    Order create(OrderType orderType, long restaurantId, long userId, List<OrderItem> items);
    Order create(OrderType orderType, long restaurantId, long userId, int tableNumber, List<OrderItem> items);
}
