package ar.edu.itba.paw.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order extends OrderItemless {
    private final List<OrderItem> items;

    public Order(int orderId, OrderType orderType, Restaurant restaurant, User user, LocalDateTime dateOrdered, LocalDateTime dateConfirmed, LocalDateTime dateReady, LocalDateTime dateDelivered, LocalDateTime dateCanceled, String address, int tableNumber, List<OrderItem> items) {
        super(orderId, orderType, restaurant, user, dateOrdered, dateConfirmed, dateReady, dateDelivered, dateCanceled, address, tableNumber, items.size(), calculatePrice(items));
        this.items = items;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    private static BigDecimal calculatePrice(Iterable<OrderItem> items) {
        BigDecimal orderPrice = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal productPrice = item.getProduct().getPrice();
            orderPrice = orderPrice.add(productPrice.multiply(quantity));
        }

        return orderPrice;
    }
}
