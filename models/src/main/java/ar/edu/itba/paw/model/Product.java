package ar.edu.itba.paw.model;

public class Product {

    private final long productId;
    private final long categoryId;
    private final String name;
    private String description;
    private long imageId;
    private final double price;
    private boolean available;

    public Product(long productId, long categoryId, String name, double price) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
    }

    public Product(long productId, long categoryId, String name, double price, String description, long imageId, boolean available) {
        this(productId, categoryId, name, price);
        this.description = description;
        this.imageId = imageId;
        this.available = available;
    }

    public long getProductId() {
        return productId;
    }

    public long getCategoryId() {
        return categoryId;
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
