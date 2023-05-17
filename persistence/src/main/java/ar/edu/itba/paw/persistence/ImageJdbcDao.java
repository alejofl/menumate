package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.persistance.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageJdbcDao implements ImageDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageJdbcDao.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("images")
                .usingGeneratedKeyColumns("image_id");
    }

    @Override
    public long create(byte[] bytes) {
        final Map<String, Object> imageData = new HashMap<>();
        imageData.put("bytes", bytes);
        int imageId = jdbcInsert.executeAndReturnKey(imageData).intValue();
        LOGGER.info("Created image with ID {}", imageId);
        return imageId;
    }

    @Override
    public Optional<byte[]> getById(long imageId) {
        return jdbcTemplate.query(
                "SELECT * FROM images WHERE image_id = ?",
                SimpleRowMappers.IMAGE_ROW_MAPPER,
                imageId
        ).stream().findFirst();
    }

    @Override
    public void update(long imageId, byte[] bytes) {
        int rows = jdbcTemplate.update(
                "UPDATE images SET bytes = ? WHERE image_id = ?",
                bytes,
                imageId
        );

        if (rows == 0)
            throw new ImageNotFoundException();

        LOGGER.info("Updated image with ID {}", imageId);
    }

    @Override
    public void delete(long imageId) {
        int rows = jdbcTemplate.update(
                "DELETE FROM images WHERE image_id = ?",
                imageId
        );

        if (rows == 0)
            throw new ImageNotFoundException();

        LOGGER.info("Deleted image with ID {}", imageId);
    }
}
