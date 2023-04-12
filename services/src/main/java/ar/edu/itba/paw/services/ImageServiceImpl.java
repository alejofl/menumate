package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao orderDao) {
        this.imageDao = orderDao;
    }

    @Override
    public Image createImage(byte[] bytes) {
        return imageDao.createImage(bytes);
    }

    @Override
    public boolean updateImage(long imageId, byte[] bytes) {
        return imageDao.updateImage(imageId, bytes);
    }

    @Override
    public boolean deleteImage(long imageId) {
        return imageDao.deleteImage(imageId);
    }

    @Override
    public Optional<Image> getImageById(long imageId) {
        return imageDao.getImageById(imageId);
    }
}
