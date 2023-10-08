package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageDao {

    Optional<Image> getById(long imageId);

    Image create(byte[] bytes);

    void update(long imageId, byte[] bytes);

    void delete(long imageId);
}
