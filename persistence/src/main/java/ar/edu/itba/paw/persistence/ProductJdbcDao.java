package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductJdbcDao implements ProductDao {

    private static final String SelectBase = "SELECT " + TableFields.PRODUCTS_FIELDS + ", " + TableFields.CATEGORIES_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + " FROM products JOIN categories ON products.category_id = categories.category_id JOIN restaurants ON categories.restaurant_id = restaurants.restaurant_id";

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
    public Product create(long categoryId, String name, double price) {
        final Map<String, Object> productData = new HashMap<>();
        productData.put("category_id", categoryId);
        productData.put("name", name);
        productData.put("price", price);

        final long productId = jdbcInsert.executeAndReturnKey(productData).longValue();
        return getById(productId).get();
    }

    @Override
    public Optional<Product> getById(long productId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE products.product_id = ?",
                RowMappers.PRODUCT_ROW_MAPPER,
                productId
        ).stream().findFirst();
    }

    @Override
    public List<Product> getByCategory(long categoryId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE products.category_id = ?",
                RowMappers.PRODUCT_ROW_MAPPER,
                categoryId
        );
    }

    @Override
    public List<Product> getByRestaurantOrderByCategoryOrder(long restaurantId) {
        return jdbcTemplate.query(
                SelectBase + " WHERE restaurants.restaurant_id = ? ORDER BY categories.order_num",
                RowMappers.PRODUCT_ROW_MAPPER,
                restaurantId
        );
    }

    @Override
    public boolean updatePrice(long productId, double price) {
        return jdbcTemplate.update("UPDATE products SET price = ? WHERE product_id = ?", price, productId) > 0;
    }

    @Override
    public boolean updateName(long productId, String name) {
        return jdbcTemplate.update("UPDATE products SET name = ? WHERE product_id = ?", name, productId) > 0;
    }

    @Override
    public boolean delete(long productId) {
        return jdbcTemplate.update("DELETE FROM products WHERE product_id = ?", productId) > 0;
    }

}
