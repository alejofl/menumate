package ar.edu.itba.paw.persistance;

import java.util.Optional;

public interface ImageDao {
    long create(byte[] bytes);

    Optional<byte[]> getById(long imageId);

    void update(long imageId, byte[] bytes);

    void delete(long imageId);
}
