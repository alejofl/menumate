package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CategoryJdbcDao implements CategoryDao {

    private static final String SELECT_BASE = "SELECT " + TableFields.CATEGORIES_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + " FROM categories JOIN restaurants ON categories.restaurant_id = restaurants.restaurant_id";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public CategoryJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("categories")
                .usingColumns("restaurant_id", "name", "order_num")
                .usingGeneratedKeyColumns("category_id");
    }

    @Transactional
    @Override
    public long create(long restaurantId, String name) {
        final Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("restaurant_id", restaurantId);
        categoryData.put("name", name);
        int order = jdbcTemplate.query(
                "SELECT MAX(order_num) AS m FROM categories WHERE deleted = false AND restaurant_id = ?",
                SimpleRowMappers.MAX_ROW_MAPPER,
                restaurantId
        ).get(0);
        categoryData.put("order_num", order + 1);

        final int categoryId = jdbcInsert.executeAndReturnKey(categoryData).intValue();
        return categoryId;
    }

    private static final String GET_BY_ID_SQL = SELECT_BASE + " WHERE categories.category_id = ?";

    @Override
    public Optional<Category> getById(long categoryId) {
        return jdbcTemplate.query(
                GET_BY_ID_SQL,
                SimpleRowMappers.CATEGORY_ROW_MAPPER,
                categoryId
        ).stream().findFirst();
    }

    private static final String GET_BY_RESTAURANT_SORTED_SQL = SELECT_BASE +
            " WHERE categories.deleted = false AND categories.restaurant_id = ? ORDER BY categories.order_num";

    @Override
    public List<Category> getByRestaurantSortedByOrder(long restaurantId) {
        RowMapper<Category> rowMapper = ReusingRowMappers.getCategoryRowMapper();
        return jdbcTemplate.query(
                GET_BY_RESTAURANT_SORTED_SQL,
                rowMapper,
                restaurantId
        );
    }

    @Override
    public void updateName(long categoryId, String name) {
        int rows = jdbcTemplate.update(
                "UPDATE categories SET name = ? WHERE deleted = false AND category_id = ?",
                name,
                categoryId
        );

        if (rows == 0)
            throw new CategoryNotFoundException();
    }

    @Override
    public void updateOrder(long categoryId, int order) {
        int rows = jdbcTemplate.update(
                "UPDATE categories SET order_num = ? WHERE deleted = false AND category_id = ?",
                order,
                categoryId
        );

        if (rows == 0)
            throw new CategoryNotFoundException();
    }

    @Transactional
    @Override
    public void delete(long categoryId) {
        int rows = jdbcTemplate.update(
                "UPDATE categories SET deleted = true WHERE deleted = false AND category_id = ?",
                categoryId
        );

        if (rows == 0)
            throw new CategoryNotFoundException();

        jdbcTemplate.update(
                "UPDATE products SET deleted = true WHERE category_id = ?",
                categoryId
        );
    }
}
