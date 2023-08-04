package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.OrderItem;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OrderItemDto {
    private int lineNumber;
    private ProductDto product;
    private int quantity;
    private String comment;

    public static OrderItemDto fromOrderItem(final OrderItem orderItem) {
        final OrderItemDto dto = new OrderItemDto();
        dto.lineNumber = orderItem.getLineNumber();
        dto.product = ProductDto.fromProduct(orderItem.getProduct());
        dto.quantity = orderItem.getQuantity();
        dto.comment = orderItem.getComment();

        return dto;
    }

    public static List<OrderItemDto> fromOrderItemCollection(final Collection<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItemDto::fromOrderItem).collect(Collectors.toList());
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
