package ar.edu.itba.paw.persistance;

import java.util.Optional;

public interface ImageDao {
    int create(byte[] bytes);

    Optional<byte[]> getById(int imageId);

    boolean update(int imageId, byte[] bytes);

    boolean delete(int imageId);
}
