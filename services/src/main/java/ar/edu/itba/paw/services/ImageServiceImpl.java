package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Override
    public long create(byte[] bytes) {
        return imageDao.create(bytes);
    }

    @Override
    public boolean update(long imageId, byte[] bytes) {
        return imageDao.update(imageId, bytes);
    }

    @Override
    public boolean delete(long imageId) {
        return imageDao.delete(imageId);
    }

    @Override
    public Optional<byte[]> getById(long imageId) {
        return imageDao.getById(imageId);
    }
}
