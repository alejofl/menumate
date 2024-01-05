package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.exception.PromotionNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.CategoryConstants;
import ar.edu.itba.paw.persistence.constants.ProductConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ProductJpaDaoTest {

    private static final long NON_EXISTENT_PRODUCT_ID = 9999L;
    private static final long NON_EXISTENT_PROMOTION_ID = 51247891L;

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
    public void testCreate() {
        final Product product = productDao.create(
                CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0],
                ProductConstants.DEFAULT_PRODUCT_NAME,
                ProductConstants.DEFAULT_PRODUCT_DESCRIPTION,
                null,
                ProductConstants.DEFAULT_PRODUCT_PRICE
        );
        em.flush();

        assertNotNull(product);
        assertEquals(ProductConstants.DEFAULT_PRODUCT_NAME, product.getName());
        assertEquals(ProductConstants.DEFAULT_PRODUCT_DESCRIPTION, product.getDescription());
        assertEquals(ProductConstants.DEFAULT_PRODUCT_PRICE, product.getPrice());
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0], product.getCategoryId());
        assertTrue(product.getAvailable());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId()));
    }

    @Test
    public void testFindProductById() {
        final Optional<Product> product = productDao.getById(ProductConstants.PRODUCT_DELETED_FROM_CATEGORY_RESTAURANT_0);

        assertTrue(product.isPresent());
        assertEquals(ProductConstants.PRODUCT_DELETED_FROM_CATEGORY_RESTAURANT_0, product.get().getProductId().longValue());
        assertEquals(ProductConstants.DEFAULT_PRODUCT_NAME, product.get().getName());
        assertEquals(ProductConstants.DEFAULT_PRODUCT_PRICE, product.get().getPrice());
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0], product.get().getCategoryId());
        assertEquals(ProductConstants.DEFAULT_PRODUCT_DESCRIPTION, product.get().getDescription());
        assertTrue(product.get().getAvailable());
        assertTrue(product.get().getDeleted());
    }

    @Test
    public void testFindProductByIdNotFound() {
        final Optional<Product> product = productDao.getById(NON_EXISTENT_PRODUCT_ID);
        assertFalse(product.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteExistingProduct() {
        productDao.delete(ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0] + " AND deleted = true"));
    }

    @Test(expected = ProductNotFoundException.class)
    public void testNoDelete() {
        productDao.delete(NON_EXISTENT_PRODUCT_ID);
        em.flush();
    }

    @Test
    @Rollback
    public void updateProduct() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        final String oldName = product.getName();
        final String oldDescription = product.getDescription();

        productDao.updateNameAndDescription(product, ProductConstants.DEFAULT_STRING, ProductConstants.DEFAULT_STRING);
        em.flush();

        assertNotEquals(oldDescription, product.getDescription());
        assertNotEquals(oldName, product.getName());
        assertEquals(ProductConstants.DEFAULT_STRING, product.getName());
        assertEquals(ProductConstants.DEFAULT_STRING, product.getDescription());
    }

    @Test
    @Rollback
    public void updateProductAndPromotions() {
        final Product product = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        final Promotion promotion = em.find(Promotion.class, ProductConstants.PROMOTION_ID);
        final String oldName = product.getName();
        final String oldDescription = product.getDescription();

        productDao.updateNameAndDescription(product, ProductConstants.DEFAULT_STRING, ProductConstants.DEFAULT_STRING);
        em.flush();
        assertNotEquals(oldDescription, promotion.getDestination().getDescription());
        assertNotEquals(oldName, promotion.getDestination().getName());
        assertEquals(ProductConstants.DEFAULT_STRING, promotion.getDestination().getDescription());
        assertEquals(ProductConstants.DEFAULT_STRING, promotion.getDestination().getName());
    }

    @Test
    public void testCreatePromotion() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        final Promotion promotion = productDao.createPromotion(
                product,
                ProductConstants.DEFAULT_PROMOTION_START_DATE,
                ProductConstants.DEFAULT_PROMOTION_END_DATE,
                ProductConstants.DEFAULT_PROMOTION_DISCOUNT
        );
        em.flush();

        assertNotNull(promotion);
        assertEquals(ProductConstants.DEFAULT_PROMOTION_START_DATE, promotion.getStartDate());
        assertEquals(ProductConstants.DEFAULT_PROMOTION_END_DATE, promotion.getEndDate());
        assertEquals(product.getProductId(), promotion.getSource().getProductId());
        assertEquals(product.getPrice().multiply(ProductConstants.DEFAULT_PROMOTION_DISCOUNT).divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR), promotion.getDestination().getPrice());
        assertTrue(promotion.getDestination().getAvailable());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "promotions", "promotion_id = " + promotion.getPromotionId()));
    }

    @Test
    @Rollback
    public void testCreatePromotionProductAlreadyExisting() {
        final Product product = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);
        final Promotion promotion = productDao.createPromotion(
                product,
                ProductConstants.DEFAULT_PROMOTION_START_DATE,
                ProductConstants.DEFAULT_PROMOTION_END_DATE,
                ProductConstants.DEFAULT_PROMOTION_DISCOUNT
        );
        em.flush();
        assertNotNull(promotion);
        assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "promotions", "source_id = " + product.getProductId()));
    }

    @Test
    @Rollback
    public void testStopPromotionByDestination() {
        Product destination = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        Product source = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);

        productDao.stopPromotion(RestaurantConstants.RESTAURANT_IDS[3], ProductConstants.PROMOTION_ID);
        em.flush();

        assertFalse(destination.getAvailable());
        assertTrue(source.getAvailable());
    }

    @Test
    @Rollback
    public void testStopPromotionBySource() {
        Product destination = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        Product source = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);

        productDao.stopPromotionsBySource(ProductConstants.PROMOTION_SOURCE_ID);
        em.flush();

        assertFalse(destination.getAvailable());
        assertTrue(source.getAvailable());
    }

    @Test(expected = PromotionNotFoundException.class)
    public void testStopPromotionBySourceInvalidId() {
        productDao.stopPromotionsBySource(NON_EXISTENT_PRODUCT_ID);
        em.flush();
    }

    @Test(expected = PromotionNotFoundException.class)
    public void testStopPromotionByDestinationInvalidId() {
        productDao.stopPromotion(RestaurantConstants.RESTAURANT_IDS[3], NON_EXISTENT_PROMOTION_ID);
        em.flush();
    }

    @Test(expected = InvalidUserArgumentException.class)
    @Rollback
    public void testStopPromotionByDestinationAlreadyEnded() {
        Promotion promotion = em.find(Promotion.class, ProductConstants.PROMOTION_ID);
        promotion.setEndDate(LocalDateTime.now().minusDays(1));

        productDao.stopPromotion(RestaurantConstants.RESTAURANT_IDS[3], ProductConstants.PROMOTION_ID);
        em.flush();
    }

    @Test
    @Rollback
    public void testStartActivePromotions() {
        Product destination = em.find(Product.class, ProductConstants.PROMOTION_DESTINATION_ID);
        Product source = em.find(Product.class, ProductConstants.PROMOTION_SOURCE_ID);
        destination.setAvailable(false);
        source.setAvailable(true);

        productDao.startActivePromotions();
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + destination.getProductId() + " AND available = true"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + source.getProductId() + " AND available = false"));
    }

    @Test
    @Rollback
    public void testStopActivePromotions() {
        Promotion promotion = em.find(Promotion.class, ProductConstants.PROMOTION_ID);
        promotion.setEndDate(LocalDateTime.now().minusDays(1));

        productDao.closeInactivePromotions();
        em.flush();

        assertFalse(promotion.getDestination().getAvailable());
        assertTrue(promotion.getSource().getAvailable());
    }

}
