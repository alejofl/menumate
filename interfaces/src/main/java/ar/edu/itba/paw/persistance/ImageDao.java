package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageDao {
    Image create(byte[] bytes);

    Optional<Image> getById(long imageId);

    boolean update(long imageId, byte[] bytes);

    boolean delete(long imageId);
}
