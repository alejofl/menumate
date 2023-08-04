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

    OrderItem createOrderItem(long restaurantId, long productId, int lineNumber, int quantity, String comment);

    Optional<Order> getById(long orderId);

    PaginatedResult<Order> get(Long userId, Long restaurantId, OrderStatus orderStatus, boolean onlyInProgress, boolean descending, int pageNumber, int pageSize);
}
