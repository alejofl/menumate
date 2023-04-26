package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.RestaurantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RestaurantJdbcDao implements RestaurantDao {

    private static final String SelectBase = "SELECT " + TableFields.RESTAURANTS_FIELDS + " FROM restaurants";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public RestaurantJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurants")
                .usingColumns("name", "email", "logo_id", "portrait_1_id", "portrait_2_id", "address", "description")
                .usingGeneratedKeyColumns("resturant_id");
    }

    @Override
    public Optional<Restaurant> getById(int restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE restaurant_id = ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                restaurantId
        ).stream().findFirst();
    }

    @Override
    public List<Restaurant> getActive(int pageNumber, int pageSize) {
        pageNumber--;
        RowMapper<Restaurant> rowMapper = SimpleRowMappers.RESTAURANT_ROW_MAPPER;
        return jdbcTemplate.query(
                SelectBase + " WHERE is_active = true ORDER BY restaurant_id LIMIT ? OFFSET ?",
                rowMapper,
                pageSize,
                pageNumber * pageSize
        );
    }

    @Override
    public List<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize) {
        pageNumber--;
        StringBuilder searchParam = new StringBuilder("%");
        for (String token : tokens) {
            searchParam.append(token).append("%");
        }

        return jdbcTemplate.query(
                SelectBase + " WHERE is_active = true AND LOWER(name) LIKE ? ORDER BY restaurant_id LIMIT ? OFFSET ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                searchParam.toString(),
                pageSize,
                pageNumber * pageSize
        );
    }

    @Override
    public Restaurant create(String name, String email) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("name", name);
        restaurantData.put("email", email);

        final int restaurantId = jdbcInsert.executeAndReturnKey(restaurantData).intValue();
        return new Restaurant(restaurantId, name, email);
    }

    @Override
    public boolean delete(int restaurantId) {
        return jdbcTemplate.update("DELETE FROM restaurants WHERE restaurant_id = ?", restaurantId) > 0;
    }
}
