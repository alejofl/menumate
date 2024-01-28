package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.ImageConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ImageJpaDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageJpaDao imageDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateImage() {
        final Image image = imageDao.create(ImageConstants.NON_EXISTING_IMAGE_INFO);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "images", "image_id = " + ImageConstants.EXISTING_IMAGE_ID));
    }

    @Test
    @Rollback
    public void testUpdateImage() {
        imageDao.update(ImageConstants.EXISTING_IMAGE_ID, ImageConstants.NON_EXISTING_IMAGE_INFO);
        em.flush();

        final Image image = em.find(Image.class, ImageConstants.EXISTING_IMAGE_ID);
        assertArrayEquals(ImageConstants.NON_EXISTING_IMAGE_INFO, image.getBytes());
    }

    @Test
    @Rollback
    public void testDeleteExistingImage() {
        imageDao.delete(ImageConstants.EXISTING_IMAGE_ID);
        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "images", "image_id = " + ImageConstants.EXISTING_IMAGE_ID));
    }

    @Test
    public void testGetExistingImageById() {
        final Optional<Image> image = imageDao.getById(ImageConstants.EXISTING_IMAGE_ID);
        assertTrue(image.isPresent());
        assertArrayEquals(ImageConstants.EXISTING_IMAGE_INFO, image.get().getBytes());
        assertEquals(Optional.of(ImageConstants.EXISTING_IMAGE_ID).get(), image.get().getImageId());
    }

    @Test
    public void testGetNonExistingImageById() {
        final Optional<Image> image = imageDao.getById(ImageConstants.NON_EXISTING_IMAGE_ID);
        assertFalse(image.isPresent());
    }

    @Test(expected = ImageNotFoundException.class)
    public void testDeleteNonExistingImage() {
        imageDao.delete(ImageConstants.NON_EXISTING_IMAGE_ID);
    }

    @Test
    public void testImageExists() {
        assertTrue(imageDao.exists(ImageConstants.EXISTING_IMAGE_ID));
    }

    @Test
    public void testImageNotExists() {
        assertFalse(imageDao.exists(ImageConstants.NON_EXISTING_IMAGE_ID));
    }
}