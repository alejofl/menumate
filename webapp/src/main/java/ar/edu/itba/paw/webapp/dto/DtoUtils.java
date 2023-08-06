package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

final class DtoUtils {
    private DtoUtils() {

    }

    /**
     * Returns a URI pointing to the given imageId, or null if imageId is null.
     */
    public static URI getImageUri(final UriInfo uriInfo, final Long imageId) {
        if (imageId == null)
            return null;
        return uriInfo.getBaseUriBuilder().path("/images").path(String.valueOf(imageId)).build();
    }
}
