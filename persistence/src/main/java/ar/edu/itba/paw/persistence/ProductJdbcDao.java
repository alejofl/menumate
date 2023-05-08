package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductJdbcDao implements ProductDao {

    private static final String SELECT_BASE = "SELECT " + TableFields.PRODUCTS_FIELDS + ", " + TableFields.CATEGORIES_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + " FROM products JOIN categories ON products.category_id = categories.category_id JOIN restaurants ON categories.restaurant_id = restaurants.restaurant_id";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ProductJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("products")
                .usingColumns("category_id", "name", "price", "description", "image_id")
                .usingGeneratedKeyColumns("product_id");
    }

    @Override
    public Product create(int categoryId, String name, String description, int imageId, BigDecimal price) {
        final Map<String, Object> productData = new HashMap<>();
        productData.put("category_id", categoryId);
        productData.put("name", name);
        productData.put("description", description);
        productData.put("image_id", imageId);
        productData.put("price", price);

        final int productId = jdbcInsert.executeAndReturnKey(productData).intValue();
        return getById(productId).get();
    }

    @Override
    public Optional<Product> getById(int productId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE products.product_id = ?",
                SimpleRowMappers.PRODUCT_ROW_MAPPER,
                productId
        ).stream().findFirst();
    }

    @Override
    public List<Product> getByCategory(int categoryId) {
        RowMapper<Product> rowMapper = ReusingRowMappers.getProductRowMapper();
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE products.category_id = ?",
                rowMapper,
                categoryId
        );
    }

    @Override
    public List<Product> getByRestaurantOrderByCategoryOrder(int restaurantId) {
        RowMapper<Product> rowMapper = ReusingRowMappers.getProductRowMapper();
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE restaurants.restaurant_id = ? ORDER BY categories.order_num",
                rowMapper,
                restaurantId
        );
    }

    @Override
    public boolean updatePrice(int productId, BigDecimal price) {
        return jdbcTemplate.update("UPDATE products SET price = ? WHERE product_id = ?", price, productId) > 0;
    }

    @Override
    public boolean updateName(int productId, String name) {
        return jdbcTemplate.update("UPDATE products SET name = ? WHERE product_id = ?", name, productId) > 0;
    }

    @Override
    public boolean delete(int productId) {
        return jdbcTemplate.update("DELETE FROM products WHERE product_id = ?", productId) > 0;
    }
}
