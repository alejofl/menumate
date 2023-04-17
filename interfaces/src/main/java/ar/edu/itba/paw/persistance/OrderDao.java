package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> getById(int orderId);

    List<Order> getByUser(int userId, int restaurantId);

    List<Order> getByOrderTypeAndRestaurant(OrderType orderType, int restaurantId);

    List<Order> getByRestaurant(int restaurantId);

    List<Order> getByRestaurantOrderedBetweenDates(int restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByRestaurantAndAddress(int restaurantId, String address);

    List<Order> getByRestaurantAndTableNumber(int restaurantId, int tableNumber);

    boolean updateAddress(int orderId, String address);

    boolean updateTableNumber(int orderId, int tableNumber);

    boolean delete(int orderId);

    Order create(OrderType orderType, int restaurantId, int userId, String address, List<OrderItem> items);

    Order create(OrderType orderType, int restaurantId, int userId, List<OrderItem> items);

    Order create(OrderType orderType, int restaurantId, int userId, int tableNumber, List<OrderItem> items);

    OrderItem createOrderItem(Product product, int lineNumber, int quantity, String comment);
}
