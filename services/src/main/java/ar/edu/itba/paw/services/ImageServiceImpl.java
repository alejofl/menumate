package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDao imageDao;

    @Transactional
    @Override
    public long create(byte[] bytes) {
        return imageDao.create(bytes);
    }

    @Transactional
    @Override
    public Optional<byte[]> getById(long imageId) {
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
}
