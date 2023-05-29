package ar.edu.itba.paw.persistance;

import java.util.Optional;

public interface ImageDao {

    Optional<byte[]> getById(long imageId);

    long create(byte[] bytes);

    void update(long imageId, byte[] bytes);

    void delete(long imageId);
}
