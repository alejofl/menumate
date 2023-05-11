package ar.edu.itba.paw.model;

import java.math.BigDecimal;

public class Product {
    private final long productId;
    private final Category category;
    private final String name;
    private String description;
    private Long imageId;
    private final BigDecimal price;
    private boolean available;
    private boolean deleted;

    public Product(long productId, Category category, String name, BigDecimal price, String description, Long imageId, boolean available, boolean deleted) {
        this.productId = productId;
        this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageId = imageId;
        this.available = available;
        this.deleted = deleted;
    }

    public long getProductId() {
        return productId;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getImageId() {
        return imageId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean getAvailable() {
        return available;
    }

    public boolean getDeleted() {
        return deleted;
    }
}
