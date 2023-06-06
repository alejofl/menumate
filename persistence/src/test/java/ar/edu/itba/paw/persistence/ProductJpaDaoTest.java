package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ProductJpaDaoTest {

    private static final long NON_EXISTENT_PRODUCT_ID = 9999L;

    @Autowired
    private DataSource ds;

    @Autowired
    private ProductDao productDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreate() throws SQLException {
        final Product product = productDao.create(
                CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0],
                ProductConstants.DEFAULT_PRODUCT_NAME,
                ProductConstants.DEFAULT_PRODUCT_DESCRIPTION,
                null,
                ProductConstants.DEFAULT_PRODUCT_PRICE
        );
        em.flush();

        Assert.assertNotNull(product);
        Assert.assertEquals(ProductConstants.DEFAULT_PRODUCT_NAME, product.getName());
        Assert.assertEquals(ProductConstants.DEFAULT_PRODUCT_DESCRIPTION, product.getDescription());
        Assert.assertEquals(ProductConstants.DEFAULT_PRODUCT_PRICE, product.getPrice());
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0].longValue(), product.getCategoryId());
        Assert.assertTrue(product.getAvailable());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId()));
    }

    @Test
    public void testFindProductById() throws SQLException {
        final Optional<Product> product = productDao.getById(ProductConstants.PRODUCT_DELETED_FROM_CATEGORY_RESTAURANT_0);

        Assert.assertTrue(product.isPresent());
        Assert.assertEquals(ProductConstants.PRODUCT_DELETED_FROM_CATEGORY_RESTAURANT_0.longValue(), product.get().getProductId().intValue());
        Assert.assertEquals(ProductConstants.DEFAULT_PRODUCT_NAME, product.get().getName());
        Assert.assertEquals(ProductConstants.DEFAULT_PRODUCT_PRICE, product.get().getPrice());
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0].longValue(), product.get().getCategoryId());
        Assert.assertEquals(ProductConstants.DEFAULT_PRODUCT_DESCRIPTION, product.get().getDescription());
        Assert.assertTrue(product.get().getAvailable());
        Assert.assertTrue(product.get().getDeleted());
    }

    @Test
    public void testFindProductByIdNotFound() throws SQLException {
        final Optional<Product> product = productDao.getById(NON_EXISTENT_PRODUCT_ID);
        Assert.assertFalse(product.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteExistingProduct() throws SQLException {
        productDao.delete(ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0] + " AND deleted = true"));
    }

    @Test(expected = ProductNotFoundException.class)
    public void testNoDelete() throws SQLException {
        productDao.delete(NON_EXISTENT_PRODUCT_ID);
        em.flush();
    }
}
