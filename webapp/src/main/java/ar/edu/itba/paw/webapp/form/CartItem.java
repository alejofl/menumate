package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CartItem {

    @NotNull(message = "{NotNull.CartItem.productId}")
    private Long productId;

    @Min(value = 1, message = "{Min.CartItem.quantity}")
    @Max(value = 100, message = "{Max.CartItem.quantity}")
    @NotNull
    private Integer quantity;

    @Size(max = 120, message = "{Size.CartItem.comment}")
    private String comment;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentTrimmedOrNull() {
        if (comment == null)
            return null;
        final String trimmed = comment.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
