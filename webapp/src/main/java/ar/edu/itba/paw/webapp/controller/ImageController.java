package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("images")
@Component
public class ImageController {
    private final ImageService imageService;

    @Autowired
    public ImageController(final ImageService imageService) {
        this.imageService = imageService;
    }

    @GET
    @Path("/{imageId:\\d+}")
    @Produces("image/jpeg")
    public Response getImage(@PathParam("imageId") int imageId) {
        byte[] array = imageService.getById(imageId).orElseThrow(ImageNotFoundException::new);
        return Response.ok(array)
                .header("Content-Disposition", "inline; filename=\"menumate_%d.jpg\"")
                .build();
    }
}

