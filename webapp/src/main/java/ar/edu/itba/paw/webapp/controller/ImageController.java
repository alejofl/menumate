package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ImageController {
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping("/images/{id:\\d+}")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        byte[] array = imageService.getById(id).orElseThrow(ImageNotFoundException::new);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", MediaType.IMAGE_JPEG_VALUE);
        headers.set("Content-Disposition", String.format("inline; filename=\"menumate_%d.jpg\"", id));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(array);
    }
}

