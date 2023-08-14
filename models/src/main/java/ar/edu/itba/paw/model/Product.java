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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false)
    private Category category;

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

    @Column(nullable = false)
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
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

    public void setPrice(BigDecimal price) {
        this.price = price;
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
