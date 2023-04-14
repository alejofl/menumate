package ar.edu.itba.paw.model;

import java.time.LocalDateTime;

public class Order {

    private final long orderId;
    private final OrderType orderType;
    private final Restaurant restaurant;
    private final User user;
    private LocalDateTime dateOrdered;
    private LocalDateTime dateDelivered;
    private String address;
    private int tableNumber;

    public Order(long orderId, OrderType orderType, Restaurant restaurant, User user) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.restaurant = restaurant;
        this.user = user;
    }

    public Order(long orderId, OrderType orderType, Restaurant restaurant, User user, LocalDateTime dateOrdered, LocalDateTime dateDelivered, String address, int tableNumber) {
        this(orderId, orderType, restaurant, user);
        this.dateOrdered = dateOrdered;
        this.dateDelivered = dateDelivered;
        this.address = address;
        this.tableNumber = tableNumber;
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

    public String getAddress() {
        return address;
    }

    public int getTableNumber() {
        return tableNumber;
    }
}
