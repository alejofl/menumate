package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageService {
    Image createImage(byte[] bytes);
    boolean updateImage(long imageId, byte[] bytes);
    boolean deleteImage(long imageId);
    Optional<Image> getImageById(long imageId);
}
