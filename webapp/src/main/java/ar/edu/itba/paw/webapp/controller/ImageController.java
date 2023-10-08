package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Arrays;

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
    @Produces("image/jpg")
    public Response getImage(@PathParam("imageId") int imageId) {
        Image image = imageService.getById(imageId).orElseThrow(ImageNotFoundException::new);
        return Response.ok(image.getBytes())
                .header("Content-Disposition", String.format("inline; filename=\"menumate_%d.jpg\"", imageId))
                .build();
    }

    @POST
    @Consumes("image/*")
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(@Context HttpServletRequest request) throws IOException {
        byte[] imageBytes = IOUtils.toByteArray(request.getInputStream());
        System.out.println(Arrays.toString(imageBytes));
        Image image = imageService.create(imageBytes);
        ImageDto imageDto = ImageDto.fromImage(uriInfo, image);
        return Response.created(UriUtils.getImageUri(uriInfo, image.getImageId())).entity(imageDto).build();
    }

    @DELETE
    @Path("/{imageId:\\d+}")
    public Response deleteImage(@PathParam("imageId") int imageId) {
        imageService.delete(imageId);
        return Response.ok().build();
    }
}

