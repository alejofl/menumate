package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistance.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageJpaDao implements ImageDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public long create(byte[] bytes) {
        final Image image = new Image(null, bytes);
        em.persist(image);
        LOGGER.info("Created image with ID {}, length {}", image.getImageId(), image.getBytes().length);
        return image.getImageId();
    }

    @Override
    public Optional<byte[]> getById(long imageId) {
        final Image image = em.find(Image.class, imageId);
        return image == null ? Optional.empty() : Optional.of(image.getBytes());
    }

    @Transactional
    @Override
    public void update(long imageId, byte[] bytes) {
        final Image image = new Image(imageId, bytes);
        em.persist(image);
        LOGGER.info("Updated image with ID {}, length is now {}", image.getImageId(), image.getBytes().length);
    }

    @Transactional
    @Override
    public void delete(long imageId) {
        em.remove(new Image(imageId, null));
        LOGGER.info("Deleted image with ID {}", imageId);
    }
}
