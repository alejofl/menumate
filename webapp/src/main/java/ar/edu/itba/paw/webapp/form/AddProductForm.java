package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.Image;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddProductForm {
    @NotNull
    private Integer categoryId;

    @NotNull
    @Size(max=150)
    private String productName;

    @Size(max=300)
    private String description;

    @Image
    private MultipartFile image;

    @NotNull
    @Min(0)
    private Double price;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
