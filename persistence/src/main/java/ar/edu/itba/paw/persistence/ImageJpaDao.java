package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistance.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageJpaDao implements ImageDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Image> getById(long imageId) {
        final Image image = em.find(Image.class, imageId);
        return image == null ? Optional.empty() : Optional.of(image);
    }

    @Override
    public Image create(byte[] bytes) {
        final Image image = new Image(null, bytes);
        em.persist(image);
        LOGGER.info("Created image with ID {}, length {}", image.getImageId(), image.getBytes().length);
        return image;
    }

    @Override
    public void update(long imageId, byte[] bytes) {
        final Image image = new Image(imageId, bytes);
        em.merge(image);
        LOGGER.info("Updated image with ID {}, length is now {}", image.getImageId(), image.getBytes().length);
    }

    @Override
    public void delete(long imageId) {
        try {
            final Image image = em.getReference(Image.class, imageId);
            em.remove(image);
            em.flush();
            LOGGER.info("Deleted image with ID {}", imageId);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Attempted to update non-existing image id {}", imageId);
            throw new ImageNotFoundException();
        }
    }

    @Override
    public boolean exists(long imageId) {
        return em.find(Image.class, imageId) != null;
    }

}
