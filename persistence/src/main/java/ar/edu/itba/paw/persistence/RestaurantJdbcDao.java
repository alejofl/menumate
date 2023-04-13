package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.RestaurantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class RestaurantJdbcDao implements RestaurantDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<Restaurant> restaurantRowMapper = (ResultSet rs, int rowNum) -> new Restaurant(
            rs.getLong("restaurant_id"),
            rs.getString("name"),
            rs.getLong("logo_id"),
            rs.getLong("portrait_1_id"),
            rs.getLong("portrait_2_id"),
            rs.getString("address"),
            rs.getString("description"),
            rs.getBoolean("is_active")
    );

    @Autowired
    public RestaurantJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurants")
                .usingColumns("name", "logo_id", "portrait_1_id", "portrait_2_id", "address", "description")
                .usingGeneratedKeyColumns("resturant_id");
    }

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM restaurants WHERE restaurant_id = ?", restaurantRowMapper, restaurantId).stream().findFirst();
    }

    @Override
    public Restaurant create(String name) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("name", name);

        final long restaurantId = jdbcInsert.executeAndReturnKey(restaurantData).longValue();
        return new Restaurant(restaurantId, name);
    }

    @Override
    public boolean delete(long restaurantId) {
        return jdbcTemplate.update("DELETE FROM restaurants WHERE restaurant_id = ?", restaurantId) > 0;
    }
}
