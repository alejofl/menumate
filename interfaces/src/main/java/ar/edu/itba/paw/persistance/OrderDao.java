package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Order createDelivery(long restaurantId, long userId, String address);

    Order createTakeaway(long restaurantId, long userId);

    Order createDineIn(long restaurantId, long userId, int tableNumber);

    OrderItem createOrderItem(Product product, int lineNumber, int quantity, String comment);

    Optional<Order> getById(long orderId);

    PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus);
}
