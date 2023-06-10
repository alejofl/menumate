package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.Optional;

public interface OrderDao {
    Order createDelivery(long restaurantId, long userId, String address);

    Order createTakeaway(long restaurantId, long userId);

    Order createDineIn(long restaurantId, long userId, int tableNumber);

    OrderItem createOrderItem(long productId, int lineNumber, int quantity, String comment);

    Optional<Order> getById(long orderId);

    PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize, boolean onlyInProgress, boolean descending);

    /**
     * Gets a restaurant's orders, optionally by status, ordered by date descending. orderStatus may be null to bring
     * all orders.
     */
    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus, boolean descending);
}
