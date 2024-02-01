package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ImageDto {

    private long imageId;
    private URI selfUri;
    public static ImageDto fromImage(final UriInfo uriInfo, final Image image){
        final ImageDto imageDto = new ImageDto();
        imageDto.selfUri = UriUtils.getImageUri(uriInfo, image.getImageId());
        imageDto.imageId = image.getImageId();
        return imageDto;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public URI getSelfUri() {
        return selfUri;
    }

    public void setSelfUri(URI selfUri) {
        this.selfUri = selfUri;
    }
}
