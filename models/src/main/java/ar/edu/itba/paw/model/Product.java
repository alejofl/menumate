package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_product_id_seq")
    @SequenceGenerator(sequenceName = "products_product_id_seq", name = "products_product_id_seq", allocationSize = 1)
    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "category_id", nullable = false, updatable = false)
    private long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_id")
    private Long imageId;

    @Column(nullable = false, updatable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false, insertable = false)
    private boolean deleted;

    Product() {

    }

    public Product(long categoryId, String name, String description, Long imageId, BigDecimal price, boolean available) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageId = imageId;
        this.price = price;
        this.available = available;
    }

    // TODO: Remove this constructor, it only remains for backwards compatibility until ORM migration is finished.
    public Product(long productId, Category category, String name, BigDecimal price, String description, Long imageId, boolean available, boolean deleted) {
        this.productId = productId;
        // this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageId = imageId;
        this.available = available;
        this.deleted = deleted;
    }

    public Long getProductId() {
        return productId;
    }

    public long getCategoryId() {
        return categoryId;
    }

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

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
