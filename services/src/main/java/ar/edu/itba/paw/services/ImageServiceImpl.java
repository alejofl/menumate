package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Image;
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
    public Image create(byte[] bytes) {
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
    public Optional<Image> getById(long imageId) {
        return imageDao.getById(imageId);
    }
}
