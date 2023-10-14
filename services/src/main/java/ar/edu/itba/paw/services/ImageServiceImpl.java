package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDao imageDao;

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Transactional
    @Override
    public Image create(byte[] bytes) {
        return imageDao.create(bytes);
    }

    @Transactional
    @Override
    public Optional<Image> getById(long imageId) {
        return imageDao.getById(imageId);
    }

    @Transactional
    @Override
    public void update(long imageId, byte[] bytes) {
        imageDao.update(imageId, bytes);
    }

    @Transactional
    @Override
    public void delete(long imageId) {
        imageDao.delete(imageId);
    }

    @Transactional
    @Override
    public boolean exists(long imageId) {
        return imageDao.exists(imageId);
    }
}
