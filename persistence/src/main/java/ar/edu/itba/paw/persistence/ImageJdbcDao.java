package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.exception.ImageNotFoundException;
import ar.edu.itba.paw.persistance.ImageDao;
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
        return jdbcInsert.executeAndReturnKey(imageData).intValue();
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
    }

    @Override
    public void delete(long imageId) {
        int rows = jdbcTemplate.update(
                "DELETE FROM images WHERE image_id = ?",
                imageId
        );

        if (rows == 0)
            throw new ImageNotFoundException();
    }
}
