package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
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

    private static final String SELECT_BASE = "SELECT " + TableFields.RESTAURANTS_FIELDS + " FROM restaurants";

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
    public Optional<Restaurant> getById(long restaurantId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE restaurant_id = ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                restaurantId
        ).stream().findFirst();
    }

    @Override
    public PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        RowMapper<Restaurant> rowMapper = SimpleRowMappers.RESTAURANT_ROW_MAPPER;
        List<Restaurant> results = jdbcTemplate.query(
                SELECT_BASE + " WHERE is_active = true ORDER BY restaurant_id LIMIT ? OFFSET ?",
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
                SELECT_BASE + " WHERE is_active = true AND LOWER(name) LIKE ? ORDER BY restaurant_id LIMIT ? OFFSET ?",
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
    public long create(String name, String email, long ownerUserId, String description, String address, int maxTables, Long logoKey, Long portrait1Kay, Long portrait2Key) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("name", name);
        restaurantData.put("email", email);
        restaurantData.put("owner_user_id", ownerUserId);
        restaurantData.put("description", description);
        restaurantData.put("address", address);
        restaurantData.put("max_tables", maxTables);
        restaurantData.put("logo_id", logoKey);
        restaurantData.put("portrait_1_id", portrait1Kay);
        restaurantData.put("portrait_2_id", portrait2Key);
        return restaurantJdbcInsert.executeAndReturnKey(restaurantData).intValue();
    }

    @Override
    public boolean delete(long restaurantId) {
        return jdbcTemplate.update("DELETE FROM restaurants WHERE restaurant_id = ?", restaurantId) > 0;
    }
}
