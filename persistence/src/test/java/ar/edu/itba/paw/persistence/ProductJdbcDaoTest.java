package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ProductJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ProductJdbcDao productDao;

    private JdbcTemplate jdbcTemplate;

    private static final long CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "CATEGORY_NAME";
    private static final long ORDER = 1;
    private static final long RESTAURANT_ID = 1;
    private static final String RESTAURANT_NAME = "RESTAURANT_NAME";
    private static final long PRODUCT_ID = 1;
    private static final String PRODUCT_NAME = "PRODUCT_NAME";
    private static final Double PRODUCT_PRICE = 1.0;

    private static final String[] PRODUCTS_NAMES = {"Product 1", "Product 2", "Product 3", "Product 4"};

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"categories", "restaurants", "products");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name) VALUES (" + RESTAURANT_ID +", '" + RESTAURANT_NAME + "')");
        jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num) VALUES (" + CATEGORY_ID +", '" + CATEGORY_NAME + "', " + RESTAURANT_ID + ", " + ORDER + ")");
    }

    @Test
    public void testCreate() throws SQLException {
        final Product product = productDao.createProduct(CATEGORY_ID, PRODUCT_NAME, PRODUCT_PRICE);

        Assert.assertNotNull(product);
        Assert.assertEquals(PRODUCT_ID, product.getProductId());
        Assert.assertEquals(PRODUCT_NAME, product.getName());
        Assert.assertEquals(PRODUCT_PRICE.intValue(), Double.valueOf(product.getPrice()).intValue());
        Assert.assertEquals(CATEGORY_ID, product.getCategoryId());
    }

    @Test
    public void testFindProductById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID +", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        final Optional<Product> product = productDao.findProductById(PRODUCT_ID);

        Assert.assertTrue(product.isPresent());
        Assert.assertEquals(PRODUCT_ID, product.get().getProductId());
        Assert.assertEquals(PRODUCT_NAME, product.get().getName());
        Assert.assertEquals(PRODUCT_PRICE.intValue(), Double.valueOf(product.get().getPrice()).intValue());
        Assert.assertEquals(CATEGORY_ID, product.get().getCategoryId());
    }

    @Test
    public void testFindProductsByCategory() throws SQLException {

        for (int i = 1; i < PRODUCTS_NAMES.length + 1; i++) {
            jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + i +", '" + PRODUCTS_NAMES[i - 1] + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        }

        final List<Product> products = productDao.findProductsByCategory(CATEGORY_ID);

        Assert.assertNotNull(products);
        Assert.assertEquals(PRODUCTS_NAMES.length, products.size());
        for (int i = 0; i < PRODUCTS_NAMES.length; i++) {
            Assert.assertEquals(PRODUCTS_NAMES[i], products.get(i).getName());
            Assert.assertEquals(PRODUCT_PRICE.intValue(), Double.valueOf(products.get(i).getPrice()).intValue());
            Assert.assertEquals(CATEGORY_ID, products.get(i).getCategoryId());
            Assert.assertEquals(i + 1, products.get(i).getProductId());
        }
    }

    @Test
    public void testUpdateProductPrice() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID +", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        Assert.assertTrue(productDao.updateProductPrice(PRODUCT_ID, PRODUCT_PRICE + 2.0));
    }

    @Test
    public void testUpdateProductName() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID +", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        Assert.assertTrue(productDao.updateProductName(PRODUCT_ID, PRODUCT_NAME + "new"));
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID +", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        Assert.assertTrue(productDao.deleteProduct(PRODUCT_ID));
    }

}
