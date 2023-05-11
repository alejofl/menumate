package ar.edu.itba.paw.service;

import java.util.Optional;

public interface ImageService {
    long create(byte[] bytes);

    Optional<byte[]> getById(long imageId);

    boolean update(long imageId, byte[] bytes);

    boolean delete(long imageId);
}
