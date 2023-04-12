package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistance.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageJdbcDao implements ImageDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<Image> imageRowMapper = (ResultSet rs, int rowNum) -> new Image(
            rs.getLong("image_id"),
            rs.getBytes("bytes")
    );

    @Autowired
    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("images")
                .usingGeneratedKeyColumns("image_id");
    }

    @Override
    public Image create(byte[] bytes) {
        final Map<String, Object> imageData = new HashMap<>();
        imageData.put("bytes", bytes);
        final long imageId = jdbcInsert.execute(imageData);
        return new Image(imageId, bytes);
    }

    @Override
    public boolean update(long imageId, byte[] bytes) {
        return jdbcTemplate.update("UPDATE images SET bytes = ? WHERE image_id = ?", bytes, imageId) > 0;
    }

    @Override
    public boolean delete(long imageId) {
        return jdbcTemplate.update("DELETE FROM images WHERE image_id = ?", imageId) > 0;
    }

    @Override
    public Optional<Image> getById(long imageId) {
        return jdbcTemplate.query("SELECT * FROM images WHERE image_id = ?", imageRowMapper, imageId).stream().findFirst();
    }
}