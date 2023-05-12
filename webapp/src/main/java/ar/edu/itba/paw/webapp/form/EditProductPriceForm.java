package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class EditProductPriceForm {
    @NotNull
    private Integer productId;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
