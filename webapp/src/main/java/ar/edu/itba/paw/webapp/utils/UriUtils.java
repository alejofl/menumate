package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public final class UriUtils {
    private UriUtils() {

    }

    /**
     * Returns a URI pointing to the given imageId, or null if imageId is null.
     */
    public static URI getImageUri(final UriInfo uriInfo, final Long imageId) {
        if (imageId == null)
            return null;
        return uriInfo.getBaseUriBuilder().path("/images").path(String.valueOf(imageId)).build();
    }

    public static URI getRestaurantUri(final UriInfo uriInfo, final long restaurantId) {
        return uriInfo.getBaseUriBuilder().path("/restaurants").path(String.valueOf(restaurantId)).build();
    }

    public static URI getUserUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path("/users").path(String.valueOf(userId)).build();
    }

    public static URI getOrderUri(final UriInfo uriInfo, final long orderId) {
        return uriInfo.getBaseUriBuilder().path("/orders").path(String.valueOf(orderId)).build();
    }

    public static URI getReviewUri(final UriInfo uriInfo, final long reviewId) {
        return uriInfo.getAbsolutePathBuilder().path("/reviews").path(String.valueOf(reviewId)).build();
    }
}
