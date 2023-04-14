package ar.edu.itba.paw.model;

public class Category {
    private final long categoryId;
    private final Restaurant restaurant;
    private final String name;
    private final long order;

    public Category(long categoryId, Restaurant restaurant, String name, long order) {
        this.categoryId = categoryId;
        this.restaurant = restaurant;
        this.name = name;
        this.order = order;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getName() {
        return name;
    }

    public long getOrder() {
        return order;
    }
}
