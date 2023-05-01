package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> getById(int orderId);

    PaginatedResult<Order> getByUser(int userId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize);

    /*List<Order> getByOrderTypeAndRestaurant(OrderType orderType, int restaurantId);

    List<Order> getByRestaurantOrderedBetweenDates(int restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> getByRestaurantAndAddress(int restaurantId, String address);

    List<Order> getByRestaurantAndTableNumber(int restaurantId, int tableNumber);*/

    boolean updateAddress(int orderId, String address);

    boolean updateTableNumber(int orderId, int tableNumber);

    boolean delete(int orderId);

    Order create(OrderType orderType, int restaurantId, int userId, String address, List<OrderItem> items);

    Order create(OrderType orderType, int restaurantId, int userId, List<OrderItem> items);

    Order create(OrderType orderType, int restaurantId, int userId, int tableNumber, List<OrderItem> items);

    OrderItem createOrderItem(Product product, int lineNumber, int quantity, String comment);
}
