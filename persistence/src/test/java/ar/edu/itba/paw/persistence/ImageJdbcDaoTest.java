package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
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
import java.util.Arrays;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageJdbcDao imageJdbcDao;

    private JdbcTemplate jdbcTemplate;

    private static final int IMAGE_ID = 1;
    private static final byte[] IMG_INFO_1= new byte[50];
    private static final byte[] IMG_INFO_2= new byte[100];

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "images");
    }

    @Test
    public void testCreateImg() throws SQLException {
        final Image image = imageJdbcDao.createImage(IMG_INFO_1);
        Assert.assertNotNull(image);
        Assert.assertEquals(IMG_INFO_1, image.getBytes());
        Assert.assertEquals(IMAGE_ID, image.getImageId());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "images"));
    }

    @Test
    public void testUpdateImg() throws SQLException {
        jdbcTemplate.update("INSERT INTO images (image_id, bytes) VALUES (?, ?)", IMAGE_ID, IMG_INFO_1);

        Assert.assertTrue(imageJdbcDao.updateImage(IMAGE_ID, IMG_INFO_2));
        Assert.assertEquals(IMAGE_ID, imageJdbcDao.getImageById(IMAGE_ID).get().getImageId());
        Assert.assertTrue(Arrays.equals(IMG_INFO_2, imageJdbcDao.getImageById(IMAGE_ID).get().getBytes()));
    }

    @Test
    public void testDeleteImg() throws SQLException {
        jdbcTemplate.update("INSERT INTO images (image_id, bytes) VALUES (?, ?)", IMAGE_ID, IMG_INFO_1);

        Assert.assertTrue(imageJdbcDao.deleteImage(IMAGE_ID));
        Assert.assertFalse(imageJdbcDao.getImageById(IMAGE_ID).isPresent());
    }

    @Test
    public void testGetImageById() throws SQLException {
        jdbcTemplate.update("INSERT INTO images (image_id, bytes) VALUES (?, ?)", IMAGE_ID, IMG_INFO_1);

        Optional<Image> image = imageJdbcDao.getImageById(IMAGE_ID);
        Assert.assertTrue(image.isPresent());
        Assert.assertEquals(IMAGE_ID, image.get().getImageId());
        Assert.assertTrue(Arrays.equals(IMG_INFO_1, imageJdbcDao.getImageById(IMAGE_ID).get().getBytes()));
    }

}
