package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CartItem {

    @NotNull
    private Long productId;

    @Min(1)
    @Max(100)
    @NotNull
    private Integer quantity;

    @Size(max = 120)
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
        String trimmed = comment.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
