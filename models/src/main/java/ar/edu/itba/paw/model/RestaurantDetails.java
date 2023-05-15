package ar.edu.itba.paw.model;

import java.util.List;

public class RestaurantDetails {
    private final Restaurant restaurant;
    private final int averageRating;
    private final int reviewCount;
    private final float averageProductPrice;
    private final List<RestaurantTags> tags;

    public RestaurantDetails(final Restaurant restaurant, final float averageRating, final int reviewCount, final float averageProductPrice, final List<RestaurantTags> tags) {
        this.restaurant = restaurant;
        this.averageRating = Math.round(averageRating);
        this.reviewCount = reviewCount;
        this.averageProductPrice = averageProductPrice;
        this.tags = tags;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public float getAverageProductPrice() {
        return averageProductPrice;
    }

    public List<RestaurantTags> getTags() {
        return tags;
    }
}
