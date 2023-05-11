package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.RestaurantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Function;

@Repository
public class RestaurantJdbcDao implements RestaurantDao {

    private static final String SELECT_BASE = "SELECT " + TableFields.RESTAURANTS_FIELDS + " FROM restaurants";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert restaurantJdbcInsert;
    private final SimpleJdbcInsert restaurantTagsJdbcInsert;

    @Autowired
    private ReviewJdbcDao reviewJdbcDao;

    @Autowired
    private ProductJdbcDao productJdbcDao;

    @Autowired
    public RestaurantJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        restaurantJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurants")
                .usingColumns("name", "email", "owner_user_id", "specialty", "logo_id", "portrait_1_id", "portrait_2_id", "address", "description")
                .usingGeneratedKeyColumns("restaurant_id");
        restaurantTagsJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurant_tags")
                .usingColumns("restaurant_id", "tag_id");
    }

    private static final String GET_BY_ID_SQL = SELECT_BASE + " WHERE restaurant_id = ?";

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return jdbcTemplate.query(
                GET_BY_ID_SQL,
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                restaurantId
        ).stream().findFirst();
    }

    private static final String GET_ACTIVE_SQL = SELECT_BASE
            + " WHERE restaurants.deleted = false AND restaurants.is_active = true" +
            " ORDER BY restaurants.date_created, restaurants.restaurant_id LIMIT ? OFFSET ?";

    @Override
    public PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        RowMapper<Restaurant> rowMapper = SimpleRowMappers.RESTAURANT_ROW_MAPPER;
        List<Restaurant> results = jdbcTemplate.query(
                GET_ACTIVE_SQL,
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
                "SELECT COUNT(*) AS c FROM restaurants WHERE deleted = false AND is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);
    }

    private static final String GET_SEARCH_RESULTS_SQL = SELECT_BASE +
            " WHERE restaurants.deleted = false AND restaurants.is_active = true" +
            " AND LOWER(restaurants.name) LIKE ? ORDER BY restaurants.restaurant_id LIMIT ? OFFSET ?";

    @Override
    public PaginatedResult<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        StringBuilder searchParam = new StringBuilder("%");
        for (String token : tokens) {
            searchParam.append(token).append("%");
        }
        String search = searchParam.toString();

        List<Restaurant> results = jdbcTemplate.query(
                GET_SEARCH_RESULTS_SQL,
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                search,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM restaurants WHERE deleted = false AND is_active = true AND LOWER(name) LIKE ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                search
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Restaurant> getSortedByName(int pageNumber, int pageSize, String sort) {
        int pageIdx = pageNumber - 1;

        List<Restaurant> results = jdbcTemplate.query(
                SELECT_BASE + " WHERE is_active = true ORDER BY restaurant_name " + sort + " LIMIT ? OFFSET ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT count(*) AS c FROM restaurants WHERE is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Restaurant> getSortedByPriceAverage(int pageNumber, int pageSize, String sort) {
        int pageIdx = pageNumber -1;

        List<Restaurant> results = jdbcTemplate.query(
                SELECT_BASE + " WHERE is_active = true LIMIT ? OFFSET ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT count(*) AS c FROM restaurants WHERE is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);

        results.sort(
            Comparator.comparing
            (
                (Function<? super Restaurant, ? extends Float>) r -> reviewJdbcDao.getRestaurantAverage(r.getRestaurantId()).getAverage(),
                sort.equalsIgnoreCase("ASC") ?
                Comparator.naturalOrder() : Comparator.reverseOrder()
            )
        );

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Restaurant> getSortedByCreationDate(int pageNumber, int pageSize, String sort) {
        int pageIdx = pageNumber -1;

        List<Restaurant> results = jdbcTemplate.query(
                SELECT_BASE + " WHERE is_active = true ORDER BY date_created " + sort + " LIMIT ? OFFSET ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT count(*) AS c FROM restaurants WHERE is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Restaurant> getSortedByAveragePrice(int pageNumber, int pageSize, String sort) {
        int pageIdx = pageNumber -1;

        List<Restaurant> results = jdbcTemplate.query(
                SELECT_BASE + " WHERE is_active = true ORDER BY date_created " + sort + " LIMIT ? OFFSET ?",
                SimpleRowMappers.RESTAURANT_ROW_MAPPER,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT count(*) AS c FROM restaurants WHERE is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);


        results.sort(
            Comparator.comparing
            (
                (Function<? super Restaurant, ? extends Double>) r ->  productJdbcDao.getRestaurantAveragePrice(r.getRestaurantId()),
                sort.equalsIgnoreCase("ASC") ?
                        Comparator.naturalOrder() : Comparator.reverseOrder()
            )
        );

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }


    @Override
    public long create(String name, String email, int specialty , long ownerUserId, String description, String address, int maxTables, Long logoKey, Long portrait1Kay, Long portrait2Key) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("name", name);
        restaurantData.put("email", email);
        restaurantData.put("specialty", specialty);
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
        boolean success = jdbcTemplate.update(
                "UPDATE restaurants SET deleted = true WHERE deleted = false AND restaurant_id = ?",
                restaurantId
        ) > 0;

        if (success) {
            jdbcTemplate.update(
                    "UPDATE categories SET deleted = true WHERE categories.restaurant_id = ?",
                    restaurantId
            );
            jdbcTemplate.update(
                    "UPDATE products SET deleted = true WHERE products.category_id IN (SELECT category_id FROM categories WHERE restaurant_id = ?)",
                    restaurantId
            );
        }

        return success;
    }

    @Override
    public List<RestaurantTags> getTags(int restaurantId) {
        return jdbcTemplate.query(
                "SELECT " + TableFields.RESTAURANT_TAGS_FIELDS + " FROM restaurant_tags WHERE restaurant_id = ?",
                SimpleRowMappers.RESTAURANT_TAGS_ROW_MAPPER,
                restaurantId
        );
    }

    @Override
    public boolean addTag(int restaurantId, int tagId) {
        final Map<String, Object> tagData = new HashMap<>();
        tagData.put("restaurant_id", restaurantId);
        tagData.put("tag_id", tagId);
        return restaurantTagsJdbcInsert.execute(tagData) > 0;
    }

    @Override
    public boolean removeTag(int restaurantId, int tagId) {
        return jdbcTemplate.update(
                "DELETE FROM restaurant_tags WHERE restaurant_id = ? AND tag_id = ?",
                restaurantId,
                tagId
        ) > 0;
    }
}
