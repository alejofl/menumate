package ar.edu.itba.paw;

import ar.edu.itba.paw.model.Restaurant;

public class RestaurantDetails {
    private final Restaurant restaurant;
    private final int averageRating;
    private final int reviewCount;
    private final float averageProductPrice;

    public RestaurantDetails(final Restaurant restaurant, final float averageRating, final int reviewCount, final float averageProductPrice) {
        this.restaurant = restaurant;
        this.averageRating = Math.round(averageRating);
        this.reviewCount = reviewCount;
        this.averageProductPrice = averageProductPrice;
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
}
