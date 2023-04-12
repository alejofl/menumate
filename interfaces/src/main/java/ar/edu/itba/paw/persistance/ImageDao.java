package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageDao {
    Image createImage(byte[] bytes);
    boolean updateImage(long imageId, byte[] bytes);
    boolean deleteImage(long imageId);
    Optional<Image> getImageById(long imageId);
}
