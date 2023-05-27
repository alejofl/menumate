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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ProductJdbcDaoTest {
    /*
    // TODO: Fix tests
    private static final long CATEGORY_ID = 581;
    private static final String CATEGORY_NAME = "Entradas";
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final int ORDER = 1;
    private static final long RESTAURANT_ID = 5123;
    private static final String RESTAURANT_NAME = "Kansas Grill & Bar";
    private static final String RESTAURANT_EMAIL = "kansas@lovelyrestaurant.com";
    private static final int MAX_TABLES = 20;
    private static final int SPECIALTY = 1;
    private static final int PRODUCT_ID = 912;
    private static final String PRODUCT_NAME = "Lomito";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("533.55");

    private static final String[] PRODUCTS_NAMES = {"Product 1", "Product 2", "Product 3", "Product 4"};

    @Autowired
    private DataSource ds;

    @Autowired
    private ProductJpaDao productDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "categories", "restaurants", "products", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, specialty, max_tables, owner_user_id) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "', " + SPECIALTY + ", " + MAX_TABLES + ", " + USER_ID + ")");
        jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num) VALUES (" + CATEGORY_ID + ", '" + CATEGORY_NAME + "', " + RESTAURANT_ID + ", " + ORDER + ")");
    }

    /// FIXME change test to match implementation
    @Test
    public void testCreate() throws SQLException {
        final Product product = productDao.create(CATEGORY_ID, PRODUCT_NAME, PRODUCT_PRICE);

        Assert.assertNotNull(product);
        Assert.assertEquals(PRODUCT_NAME, product.getName());
        Assert.assertEquals(PRODUCT_PRICE, product.getPrice(), PRODUCT_PRICE_DELTA);
        Assert.assertEquals(CATEGORY_ID, product.getCategory().getCategoryId());
    }

    @Test
    public void testFindProductById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        final Optional<Product> product = productDao.getById(PRODUCT_ID);

        Assert.assertTrue(product.isPresent());
        Assert.assertEquals(PRODUCT_ID, product.get().getProductId());
        Assert.assertEquals(PRODUCT_NAME, product.get().getName());
        Assert.assertEquals(PRODUCT_PRICE, product.get().getPrice());
        Assert.assertEquals(CATEGORY_ID, product.get().getCategory().getCategoryId());
    }

    @Test
    public void testFindProductsByCategory() throws SQLException {
        for (int i = 1; i <= PRODUCTS_NAMES.length; i++) {
            jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + i + ", '" + PRODUCTS_NAMES[i - 1] + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        }

        final List<Product> products = productDao.getByCategory(CATEGORY_ID);

        Assert.assertNotNull(products);
        Assert.assertEquals(PRODUCTS_NAMES.length, products.size());
        for (int i = 0; i < PRODUCTS_NAMES.length; i++) {
            Assert.assertEquals(PRODUCTS_NAMES[i], products.get(i).getName());
            Assert.assertEquals(PRODUCT_PRICE, products.get(i).getPrice());
            Assert.assertEquals(CATEGORY_ID, products.get(i).getCategory().getCategoryId());
            Assert.assertEquals(i + 1, products.get(i).getProductId());
        }
    }

    @Test
    public void testUpdateProductPrice() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        productDao.update(PRODUCT_ID, PRODUCT_NAME, PRODUCT_PRICE.add(BigDecimal.valueOf(2)), null);
    }

    @Test
    public void testUpdateProductName() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        productDao.update(PRODUCT_ID, PRODUCT_NAME + "- v2.0", PRODUCT_PRICE, null);
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ")");
        productDao.delete(PRODUCT_ID);
    }
     */
}
