package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class HomeIndexDto {
    private URI restaurantsUrl;
    private URI reviewsUrl;

    public static HomeIndexDto from(final UriInfo uriInfo) {
        final HomeIndexDto dto = new HomeIndexDto();
        dto.restaurantsUrl = UriUtils.getRestaurantsUri(uriInfo);
        dto.reviewsUrl = UriUtils.getReviewsUri(uriInfo);

        return dto;
    }

    public URI getRestaurantsUrl() {
        return restaurantsUrl;
    }

    public void setRestaurantsUrl(URI restaurantsUrl) {
        this.restaurantsUrl = restaurantsUrl;
    }

    public URI getReviewsUrl() {
        return reviewsUrl;
    }

    public void setReviewsUrl(URI reviewsUrl) {
        this.reviewsUrl = reviewsUrl;
    }
}
