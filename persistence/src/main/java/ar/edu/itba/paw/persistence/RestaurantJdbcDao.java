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

    private RowMapper<Restaurant> restaurantRowMapper = (ResultSet rs, int rowNum) -> new Restaurant(
            rs.getLong("restaurant_id"),
            rs.getString("name")
    );

    @Autowired
    public RestaurantJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurants")
                .usingGeneratedKeyColumns("resturant_id");
    }

    @Override
    public Optional<Restaurant> getRestaurantById(long id) {
        return jdbcTemplate.query("SELECT * FROM restaurants WHERE restaurant_id = ?", restaurantRowMapper, id).stream().findFirst();
    }

    @Override
    public Restaurant createRestaurant(String name) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("name", name);

        final int restaurantId = jdbcInsert.execute(restaurantData);
        return new Restaurant(restaurantId, name);
    }

    @Override
    public void deleteRestaurant(long id) {
        jdbcTemplate.update("DELETE FROM restaurants WHERE restaurant_id = ?", id);
    }
}
