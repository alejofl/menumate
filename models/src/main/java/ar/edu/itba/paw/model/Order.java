package ar.edu.itba.paw.model;

import java.time.LocalDateTime;

public class Order {

    private final long orderId;
    private final long orderTypeId;
    private final long restaurantId;
    private final long userId;
    private LocalDateTime orderDate;
    private String address;
    private int tableNumber;

    public Order(long orderId, long orderTypeId, long restaurantId, long userId) {
        this.orderId = orderId;
        this.orderTypeId = orderTypeId;
        this.restaurantId = restaurantId;
        this.userId = userId;
    }

    public Order(long orderId, long orderTypeId, long restaurantId, long userId, LocalDateTime orderDate, String address, int tableNumber) {
        this(orderId, orderTypeId, restaurantId, userId);
        this.orderDate = orderDate;
        this.address = address;
        this.tableNumber = tableNumber;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getOrderTypeId() {
        return orderTypeId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public long getUserId() {
        return userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}
