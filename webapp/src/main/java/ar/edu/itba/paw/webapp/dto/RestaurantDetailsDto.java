package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.RestaurantDetails;

import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantDetailsDto {
    private RestaurantDto restaurant;
    private float averageRating;
    private int reviewCount;
    private float averageProductPrice;

    public static RestaurantDetailsDto fromRestaurantDetails(final UriInfo uriInfo, final RestaurantDetails restaurantDetails) {
        final RestaurantDetailsDto dto = new RestaurantDetailsDto();
        dto.restaurant = RestaurantDto.fromRestaurant(uriInfo, restaurantDetails.getRestaurant());
        dto.averageRating = restaurantDetails.getAverageRating();
        dto.reviewCount = restaurantDetails.getReviewCount();
        dto.averageProductPrice = restaurantDetails.getAverageProductPrice();

        return dto;
    }
    public static List<RestaurantDetailsDto> fromRestaurantDetailsCollection(final UriInfo uriInfo, final Collection<RestaurantDetails> restaurantDetails) {
        return restaurantDetails.stream().map(r -> fromRestaurantDetails(uriInfo, r)).collect(Collectors.toList());
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
