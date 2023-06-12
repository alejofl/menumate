package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.exception.PromotionNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    @Test
    @Rollback
    public void updateProduct() throws SQLException {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        final String oldName = product.getName();
        final String oldDescription = product.getDescription();

        productDao.updateNameAndDescription(product, ProductConstants.DEFAULT_STRING, ProductConstants.DEFAULT_STRING);
        em.flush();

        Assert.assertNotEquals(oldDescription, product.getDescription());
        Assert.assertNotEquals(oldName, product.getName());
        Assert.assertEquals(ProductConstants.DEFAULT_STRING, product.getName());
        Assert.assertEquals(ProductConstants.DEFAULT_STRING, product.getDescription());
    }

    @Test
    @Rollback
    public void updateProductAndPromotions() throws SQLException {
        final Product product = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        final Promotion promotion = em.find(Promotion.class, ProductConstants.PROMOTION_PRODUCT_ID);
        final String oldName = product.getName();
        final String oldDescription = product.getDescription();

        productDao.updateNameAndDescription(product, ProductConstants.DEFAULT_STRING, ProductConstants.DEFAULT_STRING);
        em.flush();
        Assert.assertNotEquals(oldDescription, promotion.getDestination().getDescription());
        Assert.assertNotEquals(oldName, promotion.getDestination().getName());
        Assert.assertEquals(ProductConstants.DEFAULT_STRING, promotion.getDestination().getDescription());
        Assert.assertEquals(ProductConstants.DEFAULT_STRING, promotion.getDestination().getName());
    }

    @Test
    public void testCreatePromotion() throws SQLException {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        final Promotion promotion = productDao.createPromotion(
                product,
                ProductConstants.DEFAULT_PROMOTION_START_DATE,
                ProductConstants.DEFAULT_PROMOTION_END_DATE,
                ProductConstants.DEFAULT_PROMOTION_DISCOUNT
        );
        em.flush();

        Assert.assertNotNull(promotion);
        Assert.assertEquals(ProductConstants.DEFAULT_PROMOTION_START_DATE, promotion.getStartDate());
        Assert.assertEquals(ProductConstants.DEFAULT_PROMOTION_END_DATE, promotion.getEndDate());
        Assert.assertEquals(product.getProductId(), promotion.getSource().getProductId());
        Assert.assertEquals(product.getPrice().multiply(BigDecimal.valueOf(ProductConstants.DEFAULT_PROMOTION_DISCOUNT)), promotion.getDestination().getPrice());
        Assert.assertTrue(promotion.getDestination().getAvailable());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "promotions", "promotion_id = " + promotion.getPromotionId()));
    }

    @Test
    @Rollback
    public void testCreatePromotionProductAlreadyExisting() throws SQLException {
        final Product product = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);
        final Promotion promotion = productDao.createPromotion(
                product,
                ProductConstants.DEFAULT_PROMOTION_START_DATE,
                ProductConstants.DEFAULT_PROMOTION_END_DATE,
                ProductConstants.DEFAULT_PROMOTION_DISCOUNT
        );
        em.flush();
        Assert.assertNotNull(promotion);
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "promotions", "source_id = " + product.getProductId()));
    }

    @Test
    @Rollback
    public void testStopPromotionByDestination() throws SQLException {
        Product destination = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        Product source = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);

        productDao.stopPromotionByDestination(ProductConstants.PROMOTION_DESTINATION_ID);
        em.flush();

        Assert.assertFalse(destination.getAvailable());
        Assert.assertTrue(source.getAvailable());
    }

    @Test
    @Rollback
    public void testStopPromotionBySource() throws SQLException {
        Product destination = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        Product source = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);

        productDao.stopPromotionsBySource(ProductConstants.PROMOTION_SOURCE_ID);
        em.flush();

        Assert.assertFalse(destination.getAvailable());
        Assert.assertTrue(source.getAvailable());
    }

    @Test(expected = PromotionNotFoundException.class)
    public void testStopPromotionBySourceInvalidId() throws SQLException {
        productDao.stopPromotionsBySource(NON_EXISTENT_PRODUCT_ID);
        em.flush();
    }

    @Test(expected = PromotionNotFoundException.class)
    public void testStopPromotionByDestinationInvalidId() throws SQLException {
        productDao.stopPromotionByDestination(NON_EXISTENT_PRODUCT_ID);
        em.flush();
    }

    @Test(expected = IllegalStateException.class)
    @Rollback
    public void testStopPromotionByDestinationAlreadyEnded() throws SQLException {
        Promotion promotion = em.find(Promotion.class, ProductConstants.PROMOTION_PRODUCT_ID);
        promotion.setEndDate(LocalDateTime.now().minusDays(1));

        productDao.stopPromotionByDestination(ProductConstants.PROMOTION_DESTINATION_ID);
        em.flush();
    }

    @Test
    @Rollback
    public void testStartActivePromotions() throws SQLException {
        Product destination = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        Product source = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);
        destination.setAvailable(false);
        source.setAvailable(true);

        productDao.startActivePromotions();
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + destination.getProductId() + " AND available = true"));
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + source.getProductId() + " AND available = false"));
    }

    @Test
    @Rollback
    public void testStopActivePromotions() throws SQLException {
        Promotion promotion = em.find(Promotion.class, ProductConstants.PROMOTION_PRODUCT_ID);
        promotion.setEndDate(LocalDateTime.now().minusDays(1));

        productDao.closeInactivePromotions();
        em.flush();

        Assert.assertFalse(promotion.getDestination().getAvailable());
        Assert.assertTrue(promotion.getSource().getAvailable());
    }

}
