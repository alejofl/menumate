package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import ar.edu.itba.paw.webapp.form.UploadImageForm;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path(UriUtils.IMAGES_URL)
@Component
public class ImageController {

    private final ImageService imageService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ImageController(final ImageService imageService) {
        this.imageService = imageService;
    }

    @GET
    @Path("/{imageId:\\d+}")
    @Produces("image/jpeg")
    public Response getImage(@PathParam("imageId") int imageId) {
        final Image image = imageService.getById(imageId).orElseThrow(ImageNotFoundException::new);
        final Response.ResponseBuilder responseBuilder = Response.ok(image.getBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"menumate_%d.jpg\"", imageId));
        return ControllerUtils.setUnconditionalCache(responseBuilder).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(CustomMediaType.APPLICATION_IMAGE)
    public Response uploadImage(@Valid @BeanParam final UploadImageForm uploadImageForm) {
        final Image image = imageService.create(uploadImageForm.getBytes());
        final ImageDto imageDto = ImageDto.fromImage(uriInfo, image);
        return Response.created(UriUtils.getImageUri(uriInfo, image.getImageId())).entity(imageDto).build();
    }

    @DELETE
    @Path("/{imageId:\\d+}")
    public Response deleteImage(@PathParam("imageId") int imageId) {
        imageService.delete(imageId);
        return Response.noContent().build();
    }
}

