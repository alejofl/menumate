package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageJpaDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageJpaDao imageDao;

    private JdbcTemplate jdbcTemplate;

    private static final long IMAGE_ID = 6363;
    private static final byte[] IMG_INFO_1 = new byte[50];
    private static final byte[] IMG_INFO_2 = new byte[100];

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "images");

        Random r = new Random();
        r.nextBytes(IMG_INFO_1);
        r.nextBytes(IMG_INFO_2);
    }

    @Test
    public void testCreateImg() throws SQLException {
        final long image = imageDao.create(IMG_INFO_1);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "images"));
    }

    @Test
    public void testUpdateImg() throws SQLException {
        jdbcTemplate.update("INSERT INTO images (image_id, bytes) VALUES (?, ?)", IMAGE_ID, IMG_INFO_1);

        imageDao.update(IMAGE_ID, IMG_INFO_2);

        Optional<byte[]> maybeImage = imageDao.getById(IMAGE_ID);
        Assert.assertTrue(maybeImage.isPresent());
        Assert.assertArrayEquals(IMG_INFO_2, maybeImage.get());
    }

    @Test
    public void testDeleteImg() throws SQLException {
        jdbcTemplate.update("INSERT INTO images (image_id, bytes) VALUES (?, ?)", IMAGE_ID, IMG_INFO_1);

        imageDao.delete(IMAGE_ID);
        Assert.assertFalse(imageDao.getById(IMAGE_ID).isPresent());
    }

    @Test
    public void testGetImageById() throws SQLException {
        jdbcTemplate.update("INSERT INTO images (image_id, bytes) VALUES (?, ?)", IMAGE_ID, IMG_INFO_1);

        Optional<byte[]> image = imageDao.getById(IMAGE_ID);
        Assert.assertTrue(image.isPresent());
        Assert.assertArrayEquals(IMG_INFO_1, image.get());
    }
}
