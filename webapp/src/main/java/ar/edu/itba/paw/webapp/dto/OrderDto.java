package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Order;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDto {
    private long orderId;
    private String orderType;
    private long restaurantId;
    private long userId;
    private LocalDateTime dateOrdered;
    private LocalDateTime dateConfirmed;
    private LocalDateTime dateReady;
    private LocalDateTime dateDelivered;
    private LocalDateTime dateCancelled;
    private String orderStatus;
    private String address;
    private Integer tableNumber;
    private List<OrderItemDto> items;

    public static OrderDto fromOrder(final Order order) {
        final OrderDto dto = new OrderDto();
        dto.orderId = order.getOrderId();
        dto.orderType = order.getOrderType().getMessageCode();
        dto.restaurantId = order.getRestaurantId();
        dto.userId = order.getUserId();
        dto.dateOrdered = order.getDateOrdered();
        dto.dateCancelled = order.getDateConfirmed();
        dto.dateReady = order.getDateReady();
        dto.dateDelivered = order.getDateDelivered();
        dto.dateCancelled = order.getDateCancelled();
        dto.orderStatus = order.getOrderStatus().getMessageCode();
        dto.address = order.getAddress();
        dto.tableNumber = order.getTableNumber();
        dto.items = OrderItemDto.fromOrderItemCollection(order.getItems());

        return dto;
    }

    public static List<OrderDto> fromOrderCollection(final Collection<Order> orders) {
        return orders.stream().map(OrderDto::fromOrder).collect(Collectors.toList());
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(LocalDateTime dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public LocalDateTime getDateConfirmed() {
        return dateConfirmed;
    }

    public void setDateConfirmed(LocalDateTime dateConfirmed) {
        this.dateConfirmed = dateConfirmed;
    }

    public LocalDateTime getDateReady() {
        return dateReady;
    }

    public void setDateReady(LocalDateTime dateReady) {
        this.dateReady = dateReady;
    }

    public LocalDateTime getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(LocalDateTime dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public LocalDateTime getDateCancelled() {
        return dateCancelled;
    }

    public void setDateCancelled(LocalDateTime dateCancelled) {
        this.dateCancelled = dateCancelled;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
