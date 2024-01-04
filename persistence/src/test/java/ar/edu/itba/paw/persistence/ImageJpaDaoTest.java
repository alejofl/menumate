package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistence.config.TestConfig;
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

    private static final byte[] NON_EXISTING_IMAGE_INFO = {4, 5, 6};
    private static final long EXISTING_IMAGE_ID = 623;
    private static final long NON_EXISTING_IMAGE_ID = 1000;
    private static final byte[] EXISTING_IMAGE_INFO = {33, 18, 86};

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateImage() {
        final Image image = imageDao.create(NON_EXISTING_IMAGE_INFO);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "images", "image_id = " + EXISTING_IMAGE_ID));
    }

    @Test
    @Rollback
    public void testUpdateImage() {
        imageDao.update(EXISTING_IMAGE_ID, NON_EXISTING_IMAGE_INFO);
        em.flush();

        Image image = em.find(Image.class, EXISTING_IMAGE_ID);
        assertArrayEquals(NON_EXISTING_IMAGE_INFO, image.getBytes());
    }

    @Test
    @Rollback
    public void testDeleteExistingImage() {
        imageDao.delete(EXISTING_IMAGE_ID);
        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "images", "image_id = " + EXISTING_IMAGE_ID));
    }

    @Test
    public void testGetExistingImageById() {
        Optional<Image> image = imageDao.getById(EXISTING_IMAGE_ID);
        assertTrue(image.isPresent());
        assertArrayEquals(EXISTING_IMAGE_INFO, image.get().getBytes());
        assertEquals(Optional.of(EXISTING_IMAGE_ID).get(), image.get().getImageId());
    }

    @Test
    public void testGetNonExistingImageById() {
        Optional<Image> image = imageDao.getById(NON_EXISTING_IMAGE_ID);
        assertFalse(image.isPresent());
    }

    @Test(expected = ImageNotFoundException.class)
    public void testDeleteNonExistingImage() {
        imageDao.delete(NON_EXISTING_IMAGE_ID);
    }

    @Test
    public void testImageExists() {
        assertTrue(imageDao.exists(EXISTING_IMAGE_ID));
    }

    @Test
    public void testImageNotExists() {
        assertFalse(imageDao.exists(NON_EXISTING_IMAGE_ID));
    }
}