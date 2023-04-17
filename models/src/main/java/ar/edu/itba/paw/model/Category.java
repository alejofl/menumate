package ar.edu.itba.paw.model;

public class Category {
    private final int categoryId;
    private final Restaurant restaurant;
    private final String name;
    private final int order;

    public Category(int categoryId, Restaurant restaurant, String name, int order) {
        this.categoryId = categoryId;
        this.restaurant = restaurant;
        this.name = name;
        this.order = order;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }
}
