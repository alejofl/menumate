package ar.edu.itba.paw.service;

import java.util.Optional;

public interface ImageService {
    int create(byte[] bytes);

    Optional<byte[]> getById(int imageId);

    boolean update(int imageId, byte[] bytes);

    boolean delete(int imageId);
}
