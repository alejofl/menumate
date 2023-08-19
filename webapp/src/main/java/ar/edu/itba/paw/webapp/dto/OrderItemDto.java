package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OrderItemDto {
    private int lineNumber;
    private int quantity;
    private String comment;

    private URI productUrl;

    public static OrderItemDto fromOrderItem(final UriInfo uriInfo, final OrderItem orderItem) {
        final OrderItemDto dto = new OrderItemDto();
        dto.lineNumber = orderItem.getLineNumber();
        dto.quantity = orderItem.getQuantity();
        dto.comment = orderItem.getComment();

        dto.productUrl = UriUtils.getProductUri(uriInfo, orderItem.getProduct());

        return dto;
    }

    public static List<OrderItemDto> fromOrderItemCollection(final UriInfo uriInfo, final Collection<OrderItem> orderItems) {
        return orderItems.stream().map(o -> fromOrderItem(uriInfo, o)).collect(Collectors.toList());
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
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

    public URI getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(URI productUrl) {
        this.productUrl = productUrl;
    }
}
