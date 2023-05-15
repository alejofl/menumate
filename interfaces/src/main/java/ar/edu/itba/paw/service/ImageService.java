package ar.edu.itba.paw.service;

import java.util.Optional;

public interface ImageService {
    long create(byte[] bytes);

    Optional<byte[]> getById(long imageId);

    void update(long imageId, byte[] bytes);

    void delete(long imageId);
}
