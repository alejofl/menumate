package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class DeleteProductForm {
    @NotNull
    private Integer productId;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
