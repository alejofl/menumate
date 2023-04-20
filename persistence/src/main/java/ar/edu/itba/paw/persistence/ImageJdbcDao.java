package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
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
    public int create(byte[] bytes) {
        final Map<String, Object> imageData = new HashMap<>();
        imageData.put("bytes", bytes);
        return jdbcInsert.executeAndReturnKey(imageData).intValue();
    }

    @Override
    public boolean update(int imageId, byte[] bytes) {
        return jdbcTemplate.update("UPDATE images SET bytes = ? WHERE image_id = ?", bytes, imageId) > 0;
    }

    @Override
    public boolean delete(int imageId) {
        return jdbcTemplate.update("DELETE FROM images WHERE image_id = ?", imageId) > 0;
    }

    @Override
    public Optional<byte[]> getById(int imageId) {
        return jdbcTemplate.query(
                "SELECT * FROM images WHERE image_id = ?",
                SimpleRowMappers.IMAGE_ROW_MAPPER,
                imageId
        ).stream().findFirst();
    }
}
