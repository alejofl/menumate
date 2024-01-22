package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductForm {
    @NotBlank(message = "{NotBlank.ProductForm.name}")
    @Size(max = 150, message = "{Size.ProductForm.name}")
    private String name;

    @Size(max = 300, message = "{Size.ProductForm.description}")
    private String description;

    @NotNull(message = "{NotNull.ProductForm.price}")
    @DecimalMin(value = "0", inclusive = false, message = "{DecimalMin.ProductForm.price}")
    @Digits(integer = 8, fraction = 2, message = "{Digits.ProductForm.price}")
    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
