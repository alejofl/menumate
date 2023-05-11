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

    public Product(long productId, Category category, String name, BigDecimal price) {
        this.productId = productId;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public Product(long productId, Category category, String name, BigDecimal price, String description, Long imageId, boolean available) {
        this(productId, category, name, price);
        this.description = description;
        this.imageId = imageId;
        this.available = available;
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
}
