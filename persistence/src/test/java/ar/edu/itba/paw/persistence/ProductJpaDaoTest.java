package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ProductNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ProductJpaDaoTest {
    private static final long CATEGORY_ID = 581;
    private static final String CATEGORY_NAME = "Entradas";
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final int ORDER = 1;
    private static final boolean CATEGORY_DELETED = false;
    private static final long RESTAURANT_ID = 5123;
    private static final String RESTAURANT_NAME = "Kansas Grill & Bar";
    private static final String RESTAURANT_EMAIL = "kansas@lovelyrestaurant.com";
    private static final int MAX_TABLES = 20;
    private static final int SPECIALTY = 1;
    private static final long PRODUCT_ID = 912;
    private static final String PRODUCT_NAME = "Lomito";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("533.55");
    private static final String DESCRIPTION = "Lomito con papas fritas";
    private static final boolean PRODUCT_AVAILABLE = true;
    private static final String PRODUCT_DESCRIPTION = "Product description";
    private static final boolean IS_ACTIVE = true;
    private static final String PREFERRED_LANGUAGE = "es";
    private static final String RESTAURANT_ADDRESS = "Av. Corrientes 1234";
    private static final String RESTAURANT_DESCRIPTION = "Restaurante de comida americana";
    private static final LocalDateTime RESTAURANT_CREATION_DATE = LocalDateTime.now().minusDays(1);
    private static final boolean RESTAURANT_DELETED = false;
    private static final boolean RESTAURANT_IS_ACTIVE = true;

    @Autowired
    private DataSource ds;

    @Autowired
    private ProductJpaDao productDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "categories", "restaurants", "products", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "', " + MAX_TABLES + ", " + SPECIALTY + ", " + USER_ID + ", '" + RESTAURANT_ADDRESS + "', '" + RESTAURANT_DESCRIPTION + "', '" + Timestamp.valueOf(RESTAURANT_CREATION_DATE) + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num, deleted) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "', " + ORDER + ", " + CATEGORY_DELETED + ")");
    }

    @Test
    public void testCreate() throws SQLException {
        final Product product = productDao.create(CATEGORY_ID, PRODUCT_NAME, DESCRIPTION, null, PRODUCT_PRICE);
        em.flush();

        Assert.assertNotNull(product);
        Assert.assertEquals(PRODUCT_NAME, product.getName());
        Assert.assertEquals(DESCRIPTION, product.getDescription());
        Assert.assertEquals(PRODUCT_PRICE, product.getPrice());
        Assert.assertEquals(CATEGORY_ID, product.getCategoryId());
        Assert.assertTrue(product.getAvailable());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId()));
    }

    @Test
    public void testFindProductById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id, available, description, deleted) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ", " + PRODUCT_AVAILABLE + ", '" + PRODUCT_DESCRIPTION + "', false)");
        final Optional<Product> product = productDao.getById(PRODUCT_ID);

        Assert.assertTrue(product.isPresent());
        Assert.assertEquals(PRODUCT_ID, product.get().getProductId().intValue());
        Assert.assertEquals(PRODUCT_NAME, product.get().getName());
        Assert.assertEquals(PRODUCT_PRICE, product.get().getPrice());
        Assert.assertEquals(CATEGORY_ID, product.get().getCategoryId());
        Assert.assertEquals(PRODUCT_AVAILABLE, product.get().getAvailable());
        Assert.assertFalse(product.get().getDeleted());
        Assert.assertEquals(PRODUCT_DESCRIPTION, product.get().getDescription());
    }

    @Test
    public void testFindProductByIdNotFound() throws SQLException {
        final Optional<Product> product = productDao.getById(PRODUCT_ID);
        Assert.assertFalse(product.isPresent());
    }

    @Test
    public void testDelete() throws SQLException {
        jdbcTemplate.execute("INSERT INTO products (product_id, name, price, category_id, available, description, deleted) VALUES (" + PRODUCT_ID + ", '" + PRODUCT_NAME + "', " + PRODUCT_PRICE + ", " + CATEGORY_ID + ", " + PRODUCT_AVAILABLE + ", '" + PRODUCT_DESCRIPTION + "', false)");

        productDao.delete(PRODUCT_ID);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + PRODUCT_ID + " AND deleted = true"));
    }

    @Test(expected = ProductNotFoundException.class)
    public void testNoDelete() throws SQLException {
        productDao.delete(PRODUCT_ID + 1);
        em.flush();
    }
}
