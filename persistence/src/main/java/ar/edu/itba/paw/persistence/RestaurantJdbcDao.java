package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.util.PaginatedResult;
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
    private final SimpleJdbcInsert restaurantJdbcInsert;

    @Autowired
    public RestaurantJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        restaurantJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurants")
                .usingColumns("name", "email", "owner_user_id", "logo_id", "portrait_1_id", "portrait_2_id", "address", "description")
                .usingGeneratedKeyColumns("restaurant_id");
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
    public PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        RowMapper<Restaurant> rowMapper = SimpleRowMappers.RESTAURANT_ROW_MAPPER;
        List<Restaurant> results = jdbcTemplate.query(
                SelectBase + " WHERE is_active = true ORDER BY restaurant_id LIMIT ? OFFSET ?",
                rowMapper,
                pageSize,
                pageIdx * pageSize
        );

        int count = countActive();

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public int countActive() {
        return jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM restaurants WHERE is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);
    }

    @Override
    public PaginatedResult<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        StringBuilder searchParam = new StringBuilder("%");
        for (String token : tokens) {
            searchParam.append(token).append("%");
        }
        String search = searchParam.toString();

        List<Restaurant> results = jdbcTemplate.query(
                SelectBase + " WHERE is_active = true AND LOWER(name) LIKE ? ORDER BY restaurant_id LIMIT ? OFFSET ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                search,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT count(*) AS c FROM restaurants WHERE is_active = true AND LOWER(name) LIKE ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                search
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public int create(String name, String description, String address, String email, int userId, int logoKey, int portrait1Kay, int portrait2Key) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("name", name);
        restaurantData.put("description", description);
        restaurantData.put("address", address);
        restaurantData.put("email", email);
        restaurantData.put("logo_id", logoKey);
        restaurantData.put("portrait_1_id", portrait1Kay);
        restaurantData.put("portrait_2_id", portrait2Key);
        restaurantData.put("owner_user_id", userId);
        return restaurantJdbcInsert.executeAndReturnKey(restaurantData).intValue();
    }

    @Override
    public boolean delete(int restaurantId) {
        return jdbcTemplate.update("DELETE FROM restaurants WHERE restaurant_id = ?", restaurantId) > 0;
    }
}
