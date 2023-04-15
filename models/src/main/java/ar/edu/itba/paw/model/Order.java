package ar.edu.itba.paw.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final long orderId;
    private final OrderType orderType;
    private final Restaurant restaurant;
    private final User user;
    private final LocalDateTime dateOrdered;
    private final LocalDateTime dateDelivered;
    private final String address;
    private final int tableNumber;
    private final List<OrderItem> items;

    public Order(long orderId, OrderType orderType, Restaurant restaurant, User user, LocalDateTime dateOrdered, LocalDateTime dateDelivered, String address, int tableNumber, List<OrderItem> items) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.restaurant = restaurant;
        this.user = user;
        this.dateOrdered = dateOrdered;
        this.dateDelivered = dateDelivered;
        this.address = address;
        this.tableNumber = tableNumber;
        this.items = items;
    }

    public Order(long orderId, OrderType orderType, Restaurant restaurant, User user) {
        this(orderId, orderType, restaurant, user, null, null, null, 1, null);
    }

    public long getOrderId() {
        return orderId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDateOrdered() {
        return dateOrdered;
    }

    public LocalDateTime getDateDelivered() {
        return dateDelivered;
    }

    public String getAddress() {
        return address;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
