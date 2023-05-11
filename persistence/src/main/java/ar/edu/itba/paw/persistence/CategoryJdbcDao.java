package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
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
public class CategoryJdbcDao implements CategoryDao {

    private static final String SELECT_BASE = "SELECT " + TableFields.CATEGORIES_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + " FROM categories JOIN restaurants ON categories.restaurant_id = restaurants.restaurant_id";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public CategoryJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("categories")
                .usingGeneratedKeyColumns("category_id");
    }

    @Override
    public Category create(long restaurantId, String name) {
        final Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("restaurant_id", restaurantId);
        categoryData.put("name", name);
        int order = jdbcTemplate.query(
                "SELECT MAX(order_num) AS m FROM categories WHERE restaurant_id = ?",
                SimpleRowMappers.MAX_ROW_MAPPER,
                restaurantId
        ).get(0);
        categoryData.put("order_num", order + 1);

        final int categoryId = jdbcInsert.executeAndReturnKey(categoryData).intValue();
        return getById(categoryId).get();
    }

    @Override
    public Optional<Category> getById(long categoryId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE categories.category_id = ?",
                SimpleRowMappers.CATEGORY_ROW_MAPPER,
                categoryId
        ).stream().findFirst();
    }

    @Override
    public List<Category> getByRestaurantSortedByOrder(long restaurantId) {
        RowMapper<Category> rowMapper = ReusingRowMappers.getCategoryRowMapper();
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE categories.restaurant_id = ? ORDER BY categories.order_num",
                rowMapper,
                restaurantId
        );
    }

    @Override
    public boolean updateName(long categoryId, String name) {
        return jdbcTemplate.update("UPDATE categories SET name = ? WHERE category_id = ?", name, categoryId) > 0;
    }

    @Override
    public boolean updateOrder(long categoryId, int order) {
        return jdbcTemplate.update("UPDATE categories SET order_num = ? WHERE category_id = ?", order, categoryId) > 0;
    }

    @Override
    public boolean delete(long categoryId) {
        return jdbcTemplate.update("DELETE FROM categories WHERE category_id = ?", categoryId) > 0;
    }
}
