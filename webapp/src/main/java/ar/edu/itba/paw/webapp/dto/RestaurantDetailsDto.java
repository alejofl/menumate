package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.RestaurantDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantDetailsDto {
    private RestaurantDto restaurant;
    private float averageRating;
    private int reviewCount;
    private float averageProductPrice;

    public static RestaurantDetailsDto fromRestaurantDetails(final RestaurantDetails restaurantDetails) {
        final RestaurantDetailsDto dto = new RestaurantDetailsDto();
        dto.restaurant = RestaurantDto.fromRestaurant(restaurantDetails.getRestaurant());
        dto.averageRating = restaurantDetails.getAverageRating();
        dto.reviewCount = restaurantDetails.getReviewCount();
        dto.averageProductPrice = restaurantDetails.getAverageProductPrice();

        return dto;
    }
    public static List<RestaurantDetailsDto> fromRestaurantDetailsCollection(final Collection<RestaurantDetails> restaurantDetails) {
        return restaurantDetails.stream().map(RestaurantDetailsDto::fromRestaurantDetails).collect(Collectors.toList());
    }

    public RestaurantDto getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDto restaurant) {
        this.restaurant = restaurant;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public float getAverageProductPrice() {
        return averageProductPrice;
    }

    public void setAverageProductPrice(float averageProductPrice) {
        this.averageProductPrice = averageProductPrice;
    }
}
