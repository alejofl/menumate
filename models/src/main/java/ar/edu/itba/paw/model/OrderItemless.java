package ar.edu.itba.paw.model;

import java.time.LocalDateTime;

public class OrderItemless {
    private final int orderId;
    private final OrderType orderType;
    private final Restaurant restaurant;
    private final User user;
    private final LocalDateTime dateOrdered;
    private final LocalDateTime dateConfirmed;
    private final LocalDateTime dateReady;
    private final LocalDateTime dateDelivered;
    private final LocalDateTime dateCanceled;
    private final String address;
    private final int tableNumber;
    private final int itemCount;
    private final double price;

    public OrderItemless(int orderId, OrderType orderType, Restaurant restaurant, User user, LocalDateTime dateOrdered,LocalDateTime dateConfirmed, LocalDateTime dateReady, LocalDateTime dateDelivered, LocalDateTime dateCanceled, String address, int tableNumber, int itemCount, double price) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.restaurant = restaurant;
        this.user = user;
        this.dateOrdered = dateOrdered;
        this.dateConfirmed = dateConfirmed;
        this.dateReady = dateReady;
        this.dateDelivered = dateDelivered;
        this.dateCanceled = dateCanceled;
        this.address = address;
        this.tableNumber = tableNumber;
        this.itemCount = itemCount;
        this.price = price;
    }

    public int getOrderId() {
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

    public LocalDateTime getDateConfirmed() {
        return dateConfirmed;
    }

    public LocalDateTime getDateReady() {
        return dateReady;
    }

    public LocalDateTime getDateDelivered() {
        return dateDelivered;
    }

    public LocalDateTime getDateCanceled() {
        return dateCanceled;
    }

    public String getAddress() {
        return address;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getItemCount() {
        return itemCount;
    }

    public double getPrice() {
        return price;
    }

    public OrderStatus getOrderStatus() {
        if (dateCanceled != null) {
            return dateConfirmed == null ? OrderStatus.REJECTED : OrderStatus.CANCELLED;
        }

        if (dateDelivered != null)
            return OrderStatus.DELIVERED;
        if (dateReady != null)
            return OrderStatus.READY;
        if (dateConfirmed != null)
            return OrderStatus.CONFIRMED;
        return OrderStatus.PENDING;
    }
}
