package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDao imageDao;

    @Override
    public long create(byte[] bytes) {
        return imageDao.create(bytes);
    }

    @Override
    public Optional<byte[]> getById(long imageId) {
        return imageDao.getById(imageId);
    }

    @Override
    public void update(long imageId, byte[] bytes) {
        imageDao.update(imageId, bytes);
    }

    @Override
    public void delete(long imageId) {
        imageDao.delete(imageId);
    }
}
