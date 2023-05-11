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
    public Product create(long categoryId, String name, String description, Long imageId, BigDecimal price) {
        final Map<String, Object> productData = new HashMap<>();
        productData.put("category_id", categoryId);
        productData.put("name", name);
        productData.put("description", description);
        productData.put("image_id", imageId);
        productData.put("price", price);

        final int productId = jdbcInsert.executeAndReturnKey(productData).intValue();
        return getById(productId).get();
    }

    private static final String GET_BY_ID_SQL = SELECT_BASE + " WHERE products.product_id = ?";

    @Override
    public Optional<Product> getById(long productId) {
        return jdbcTemplate.query(
                GET_BY_ID_SQL,
                SimpleRowMappers.PRODUCT_ROW_MAPPER,
                productId
        ).stream().findFirst();
    }

    private static final String GET_BY_CATEGORY_SQL = SELECT_BASE + " WHERE products.deleted = false AND products.category_id = ?";

    @Override
    public List<Product> getByCategory(long categoryId) {
        RowMapper<Product> rowMapper = ReusingRowMappers.getProductRowMapper();
        return jdbcTemplate.query(
                GET_BY_CATEGORY_SQL,
                rowMapper,
                categoryId
        );
    }

    @Override
    public boolean update(long productId, String name, BigDecimal price, String description) {
        return (jdbcTemplate.update(
                "UPDATE products SET deleted = true WHERE product_id = ? AND deleted = false",
                productId
        ) > 0) && (jdbcTemplate.update(
                "INSERT INTO products (category_id, name, price, description, image_id, available) SELECT category_id, ?, ?, ?, image_id, available FROM products WHERE product_id = ?",
                name,
                price,
                description,
                productId
        ) > 0);
    }

    @Override
    public boolean delete(long productId) {
        return jdbcTemplate.update(
                "UPDATE products SET deleted = true WHERE product_id = ? AND deleted = false"
                , productId
        ) > 0;
    }
}
