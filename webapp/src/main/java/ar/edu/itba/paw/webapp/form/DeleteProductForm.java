package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class DeleteProductForm {
    @NotNull
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
