package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderDto {
    private long orderId;
    private String orderType;
    private LocalDateTime dateOrdered;
    private LocalDateTime dateConfirmed;
    private LocalDateTime dateReady;
    private LocalDateTime dateDelivered;
    private LocalDateTime dateCancelled;
    private String status;
    private String address;
    private Integer tableNumber;
    private URI selfUrl;
    private URI itemsUrl;
    private URI userUrl;
    private URI restaurantUrl;

    public static OrderDto fromOrder(final UriInfo uriInfo, final Order order) {
        final OrderDto dto = new OrderDto();
        dto.orderId = order.getOrderId();
        dto.orderType = order.getOrderType().getMessageCode();
        dto.dateOrdered = order.getDateOrdered();
        dto.dateCancelled = order.getDateConfirmed();
        dto.dateReady = order.getDateReady();
        dto.dateDelivered = order.getDateDelivered();
        dto.dateCancelled = order.getDateCancelled();
        dto.status = order.getOrderStatus().getMessageCode();
        dto.address = order.getAddress();
        dto.tableNumber = order.getTableNumber();

        dto.selfUrl = UriUtils.getOrderUri(uriInfo, order.getOrderId());
        dto.itemsUrl = UriUtils.getOrderItemsUri(uriInfo, order.getOrderId());
        dto.userUrl = UriUtils.getUserUri(uriInfo, order.getUserId());
        dto.restaurantUrl = UriUtils.getRestaurantUri(uriInfo, order.getRestaurantId());

        return dto;
    }

    public static List<OrderDto> fromOrderCollection(final UriInfo uriInfo, final Collection<Order> orders) {
        return orders.stream().map(o -> fromOrder(uriInfo, o)).collect(Collectors.toList());
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getItemsUrl() {
        return itemsUrl;
    }

    public void setItemsUrl(URI itemsUrl) {
        this.itemsUrl = itemsUrl;
    }

    public URI getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(URI userUrl) {
        this.userUrl = userUrl;
    }

    public URI getRestaurantUrl() {
        return restaurantUrl;
    }

    public void setRestaurantUrl(URI restaurantUrl) {
        this.restaurantUrl = restaurantUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderType, dateOrdered, dateConfirmed, dateReady, dateDelivered, dateCancelled, status, address, tableNumber);
    }
}
