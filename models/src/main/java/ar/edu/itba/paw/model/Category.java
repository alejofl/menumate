package ar.edu.itba.paw.model;

public class Category {
    private final long categoryId;
    private final long restaurantId;
    private String name;
    private long order;

    public Category(long categoryId, long restaurantId, String name, long order) {
        this.categoryId = categoryId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.order = order;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public long getOrder() {
        return order;
    }
}
