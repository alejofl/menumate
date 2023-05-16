package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RestaurantJdbcDao implements RestaurantDao {

    private static final String SELECT_BASE = "SELECT " + TableFields.RESTAURANTS_FIELDS + " FROM restaurants";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert restaurantJdbcInsert;
    private final SimpleJdbcInsert restaurantTagsJdbcInsert;

    @Autowired
    public RestaurantJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        restaurantJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("restaurants")
                .usingColumns("name", "email", "owner_user_id", "specialty", "logo_id", "portrait_1_id", "portrait_2_id", "address", "description", "max_tables")
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

    private static final String GET_ACTIVE_SQL = SELECT_BASE  +
            " WHERE restaurants.deleted = false AND restaurants.is_active = true" +
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

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM restaurants WHERE deleted = false AND is_active = true",
                SimpleRowMappers.COUNT_ROW_MAPPER
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    private static final String RESTAURANT_RATINGS_COUNTS_SQL = "SELECT" +
            " restaurants.restaurant_id AS restaurant_id," +
            " COALESCE(AVG(CAST(order_reviews.rating AS FLOAT)), 0) AS rating_average," +
            " COUNT(order_reviews) AS rating_count" +
            " FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id" +
            " RIGHT OUTER JOIN restaurants ON restaurants.restaurant_id = orders.restaurant_id" +
            " GROUP BY restaurants.restaurant_id";

    private static final String RESTAURANT_AVERAGE_PRICE_SQL = "SELECT COALESCE(AVG(products.price), 0)" +
            " FROM products JOIN categories ON products.category_id = categories.category_id" +
            " WHERE categories.restaurant_id = restaurants.restaurant_id" +
            " AND products.deleted = false AND products.available = true";

    private String getOrderByColumn(RestaurantOrderBy orderBy) {
        if (orderBy == null)
            return "restaurants.restaurant_id";

        switch (orderBy) {
            case DATE:
                return "restaurants.date_created";
            case ALPHABETIC:
                return "LOWER(restaurants.name)";
            case RATING:
                return "restaurant_average_rating";
            case PRICE:
                return "restaurant_average_price";
            default:
                throw new NotImplementedException();
        }
    }

    private final RowMapper<RestaurantDetails> RESTAURANT_DETAILS_ROW_MAPPER = (rs, rowNum) -> new RestaurantDetails(
            SimpleRowMappers.RESTAURANT_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getFloat("restaurant_average_rating"),
            rs.getInt("restaurant_review_count"),
            rs.getFloat("restaurant_average_price"),
            getTags(rs.getLong("restaurant_id"))
    );

    @Override
    public PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties) {
        int pageIdx = pageNumber - 1;

        if (query == null)
            query = "";

        String orderByDirection = descending ? "DESC" : "ASC";
        String orderByColumn = getOrderByColumn(orderBy);
        String specialtiesDirective = "";
        String tagsDirective = "";

        if (specialties != null && !specialties.isEmpty())
            if (specialties.size() == 1) {
                specialtiesDirective = " AND restaurants.specialty = " + specialties.get(0).ordinal();
            } else {
                specialtiesDirective = " AND restaurants.specialty IN (" + specialties.stream().map(specialty -> String.valueOf(specialty.ordinal())).collect(Collectors.joining(", ")) + ")";
            }

        if (tags != null && !tags.isEmpty()) {
            if (tags.size() == 1) {
                tagsDirective = " AND restaurants.restaurant_id IN (SELECT restaurant_id FROM restaurant_tags WHERE restaurant_tags.tag_id = " + tags.get(0).ordinal() + ") ";
            } else {
                tagsDirective = " AND restaurants.restaurant_id IN (SELECT restaurant_id FROM restaurant_tags WHERE restaurant_tags.tag_id IN (" + tags.stream().map(tag -> String.valueOf(tag.ordinal())).collect(Collectors.joining(", ")) + ")) ";
            }
        }

        // TODO: Optimize string construction to use StringBuilder
        String sql = "WITH restaurant_ratings_counts AS" +
                " (" + RESTAURANT_RATINGS_COUNTS_SQL + ")" +
                " SELECT " + TableFields.RESTAURANTS_FIELDS + ", restaurant_ratings_counts.*," +
                " restaurant_ratings_counts.rating_average AS restaurant_average_rating," +
                " restaurant_ratings_counts.rating_count AS restaurant_review_count," +
                " (" + RESTAURANT_AVERAGE_PRICE_SQL + ") AS restaurant_average_price" +
                " FROM restaurants JOIN restaurant_ratings_counts ON restaurants.restaurant_id = restaurant_ratings_counts.restaurant_id" +
                " WHERE restaurants.deleted = false AND restaurants.is_active = true AND LOWER(restaurants.name) LIKE ?" +
                specialtiesDirective + tagsDirective +
                " ORDER BY " + orderByColumn + " " + orderByDirection + (orderBy == null ? "" : ", restaurants.restaurant_id") + " LIMIT ? OFFSET ?";

        String[] tokens = query.trim().toLowerCase().split(" +");

        StringBuilder searchParam = new StringBuilder("%");
        for (String token : tokens)
            searchParam.append(token.trim()).append("%");
        String search = searchParam.toString();

        List<RestaurantDetails> results = jdbcTemplate.query(
                sql,
                RESTAURANT_DETAILS_ROW_MAPPER,
                search,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM restaurants WHERE deleted = false AND is_active = true AND LOWER(name) LIKE ?" + specialtiesDirective + tagsDirective,
                SimpleRowMappers.COUNT_ROW_MAPPER,
                search
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public long create(String name, String email, int specialty, long ownerUserId, String description, String address, int maxTables, Long logoKey, Long portrait1Kay, Long portrait2Key) {
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

    @Transactional
    @Override
    public void delete(long restaurantId) {
        int rows = jdbcTemplate.update(
                "UPDATE restaurants SET deleted = true WHERE deleted = false AND restaurant_id = ?",
                restaurantId
        );

        if (rows == 0)
            throw new RestaurantNotFoundException();

        jdbcTemplate.update(
                "UPDATE categories SET deleted = true WHERE categories.restaurant_id = ?",
                restaurantId
        );

        jdbcTemplate.update(
                "UPDATE products SET deleted = true WHERE products.category_id IN (SELECT category_id FROM categories WHERE restaurant_id = ?)",
                restaurantId
        );
    }

    @Override
    public List<RestaurantTags> getTags(long restaurantId) {
        return jdbcTemplate.query(
                "SELECT " + TableFields.RESTAURANT_TAGS_FIELDS + " FROM restaurant_tags WHERE restaurant_id = ?",
                SimpleRowMappers.RESTAURANT_TAGS_ROW_MAPPER,
                restaurantId
        );
    }

    @Override
    public void addTag(long restaurantId, long tagId) {
        final Map<String, Object> tagData = new HashMap<>();
        tagData.put("restaurant_id", restaurantId);
        tagData.put("tag_id", tagId);

        int rows = restaurantTagsJdbcInsert.execute(tagData);
        if (rows == 0)
            throw new RestaurantNotFoundException();
    }

    @Override
    public void removeTag(long restaurantId, long tagId) {
        int rows = jdbcTemplate.update(
                "DELETE FROM restaurant_tags WHERE restaurant_id = ? AND tag_id = ?",
                restaurantId,
                tagId
        );

        if (rows == 0)
            throw new RestaurantNotFoundException();
    }
}
