package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_order_id_seq")
    @SequenceGenerator(sequenceName = "orders_order_id_seq", name = "orders_order_id_seq", allocationSize = 1)
    @Column(name = "order_id")
    private long orderId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "order_type")
    private OrderType orderType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date_ordered", insertable = false, updatable = false)
    private LocalDateTime dateOrdered;

    @Column(name = "date_confirmed")
    private LocalDateTime dateConfirmed;

    @Column(name = "date_ready")
    private LocalDateTime dateReady;

    @Column(name = "date_delivered")
    private LocalDateTime dateDelivered;

    @Column(name = "date_cancelled")
    private LocalDateTime dateCancelled;

    @Column
    private String address;

    @Column(name = "table_number")
    private Integer tableNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    Order() {
    }

    public Order(OrderType orderType, Restaurant restaurant, User user, String address, Integer tableNumber) {
        this.orderType = orderType;
        this.restaurant = restaurant;
        this.user = user;
        this.dateOrdered = null;
        this.dateConfirmed = null;
        this.dateReady = null;
        this.dateDelivered = null;
        this.dateCancelled = null;
        this.address = address;
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
    }

    // TODO: REMOVE THIS CONSTRUCTOR, It's only here for backwards compatibility until ORM migration is finished.
    public Order(long orderId, OrderType orderType, Restaurant restaurant, User user, LocalDateTime dateOrdered, LocalDateTime dateConfirmed, LocalDateTime dateReady, LocalDateTime dateDelivered, LocalDateTime dateCanceled, String address, int tableNumber, List<OrderItem> items) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.restaurant = restaurant;
        this.user = user;
        this.dateOrdered = dateOrdered;
        this.dateConfirmed = dateConfirmed;
        this.dateReady = dateReady;
        this.dateDelivered = dateDelivered;
        this.dateCancelled = dateCanceled;
        this.address = address;
        this.tableNumber = tableNumber;
        this.items = items;
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

    public LocalDateTime getDateConfirmed() {
        return dateConfirmed;
    }

    public LocalDateTime getDateReady() {
        return dateReady;
    }

    public LocalDateTime getDateDelivered() {
        return dateDelivered;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setDateConfirmed(LocalDateTime dateConfirmed) {
        this.dateConfirmed = dateConfirmed;
    }

    public void setDateReady(LocalDateTime dateReady) {
        this.dateReady = dateReady;
    }

    public void setDateDelivered(LocalDateTime dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public void setDateCancelled(LocalDateTime dateCancelled) {
        this.dateCancelled = dateCancelled;
    }

    public LocalDateTime getDateCancelled() {
        return dateCancelled;
    }

    public String getAddress() {
        return address;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public BigDecimal getPrice() {
        return calculatePrice(items);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
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

    public OrderStatus getOrderStatus() {
        if (dateCancelled != null) {
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
