package ar.edu.itba.paw.model;

public class Product {

    private final long productId;
    private final Category category;
    private final String name;
    private String description;
    private long imageId;
    private final double price;
    private boolean available;

    public Product(long productId, Category category, String name, double price) {
        this.productId = productId;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public Product(long productId, Category category, String name, double price, String description, long imageId, boolean available) {
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

    public long getImageId() {
        return imageId;
    }

    public double getPrice() {
        return price;
    }

    public boolean getAvailable() {
        return available;
    }
}
