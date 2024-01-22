package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class HomeIndexDto {
    private URI restaurantsUrl;
    private URI usersUrl;
    private URI ordersUrl;
    private URI reviewsUrl;
    private URI imagesUrl;

    public static HomeIndexDto from(final UriInfo uriInfo) {
        final HomeIndexDto dto = new HomeIndexDto();
        dto.restaurantsUrl = UriUtils.getRestaurantsUri(uriInfo);
        dto.usersUrl = UriUtils.getUsersUri(uriInfo);
        dto.ordersUrl = UriUtils.getOrdersUri(uriInfo);
        dto.reviewsUrl = UriUtils.getReviewsUri(uriInfo);
        dto.imagesUrl = UriUtils.getImagesUri(uriInfo);

        return dto;
    }

    public URI getRestaurantsUrl() {
        return restaurantsUrl;
    }

    public void setRestaurantsUrl(URI restaurantsUrl) {
        this.restaurantsUrl = restaurantsUrl;
    }

    public URI getUsersUrl() {
        return usersUrl;
    }

    public void setUsersUrl(URI usersUrl) {
        this.usersUrl = usersUrl;
    }

    public URI getOrdersUrl() {
        return ordersUrl;
    }

    public void setOrdersUrl(URI ordersUrl) {
        this.ordersUrl = ordersUrl;
    }

    public URI getReviewsUrl() {
        return reviewsUrl;
    }

    public void setReviewsUrl(URI reviewsUrl) {
        this.reviewsUrl = reviewsUrl;
    }

    public URI getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(URI imagesUrl) {
        this.imagesUrl = imagesUrl;
    }
}
