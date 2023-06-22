package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
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
    private static final byte[] EXISTING_IMAGE_INFO = {33, 18, 86};

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateImg() {
        final long image = imageDao.create(NON_EXISTING_IMAGE_INFO);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "images", "image_id = " + image));
    }

    @Test
    @Rollback
    public void testUpdateImg() {
        imageDao.update(EXISTING_IMAGE_ID, NON_EXISTING_IMAGE_INFO);
        em.flush();

        Image image = em.find(Image.class, EXISTING_IMAGE_ID);
        Assert.assertArrayEquals(NON_EXISTING_IMAGE_INFO, image.getBytes());
    }

    @Test
    @Rollback
    public void testDeleteImg() {
        imageDao.delete(EXISTING_IMAGE_ID);
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "images", "image_id = " + EXISTING_IMAGE_ID));
    }

    @Test
    public void testGetImageById() {
        Optional<byte[]> image = imageDao.getById(EXISTING_IMAGE_ID);
        Assert.assertTrue(image.isPresent());
        Assert.assertArrayEquals(EXISTING_IMAGE_INFO, image.get());
    }
}