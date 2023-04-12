package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CategoryJdbcDao implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<Category> categoryRowMapper = (ResultSet rs, int rowNum) -> new Category(
            rs.getLong("category_id"),
            rs.getLong("restaurant_id"),
            rs.getString("name"),
            rs.getLong("order_num")
    );

    @Autowired
    public CategoryJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("categories")
                .usingGeneratedKeyColumns("category_id");
    }

    @Override
    public Category createCategory(long restaurantId, String name, long order) {
        final Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("restaurant_id", restaurantId);
        categoryData.put("name", name);
        categoryData.put("order_num", order);

        final int categoryId = jdbcInsert.execute(categoryData);
        return new Category(categoryId, restaurantId, name, order);
    }

    @Override
    public Optional<Category> getCategoryById(long categoryId) {
        return jdbcTemplate.query("SELECT * FROM categories WHERE category_id = ?", categoryRowMapper, categoryId).stream().findFirst();
    }

    @Override
    public List<Category> findByRestaurantId(long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM categories WHERE restaurant_id = ?", categoryRowMapper, restaurantId);
    }

    @Override
    public boolean updateName(long categoryId, String name) {
        return jdbcTemplate.update("UPDATE categories SET name = ? WHERE category_id = ?", name, categoryId) > 0;
    }

    @Override
    public boolean updateOrder(long categoryId, long order) {
        return jdbcTemplate.update("UPDATE categories SET order_num = ? WHERE category_id = ?", order, categoryId) > 0;
    }

    @Override
    public boolean deleteCategory(long categoryId) {
        return jdbcTemplate.update("DELETE FROM categories WHERE category_id = ?", categoryId) > 0;
    }
}
