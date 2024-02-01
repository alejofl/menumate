package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class HomeIndexDto {
    private String restaurantsUriTemplate;
    private URI usersUrl;
    private String ordersUriTemplate;
    private String reviewsUriTemplate;
    private URI imagesUrl;

    public static HomeIndexDto from(final UriInfo uriInfo) {
        final HomeIndexDto dto = new HomeIndexDto();
        dto.restaurantsUriTemplate = UriUtils.getRestaurantsUriTemplate(uriInfo);
        dto.usersUrl = UriUtils.getUsersUri(uriInfo);
        dto.ordersUriTemplate = UriUtils.getOrdersUriTemplate(uriInfo);
        dto.reviewsUriTemplate = UriUtils.getReviewsUriTemplate(uriInfo);
        dto.imagesUrl = UriUtils.getImagesUri(uriInfo);

        return dto;
    }

    public String getRestaurantsUriTemplate() {
        return restaurantsUriTemplate;
    }

    public void setRestaurantsUriTemplate(String restaurantsUriTemplate) {
        this.restaurantsUriTemplate = restaurantsUriTemplate;
    }

    public URI getUsersUrl() {
        return usersUrl;
    }

    public void setUsersUrl(URI usersUrl) {
        this.usersUrl = usersUrl;
    }

    public String getOrdersUriTemplate() {
        return ordersUriTemplate;
    }

    public void setOrdersUriTemplate(String ordersUriTemplate) {
        this.ordersUriTemplate = ordersUriTemplate;
    }

    public String getReviewsUriTemplate() {
        return reviewsUriTemplate;
    }

    public void setReviewsUriTemplate(String reviewsUriTemplate) {
        this.reviewsUriTemplate = reviewsUriTemplate;
    }

    public URI getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(URI imagesUrl) {
        this.imagesUrl = imagesUrl;
    }
}
