package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageService {
    Image create(byte[] bytes);

    Optional<Image> getById(long imageId);

    void update(long imageId, byte[] bytes);

    void delete(long imageId);
}
