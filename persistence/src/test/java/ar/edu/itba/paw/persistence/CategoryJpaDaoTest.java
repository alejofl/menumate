package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.CategoryDeletedException;
import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CategoryJpaDaoTest {

    private static final long NON_EXISTING_CATEGORY_ID = 10000;

    @Autowired
    private DataSource ds;

    @Autowired
    private CategoryJpaDao categoryDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreate() {
        final Category category = categoryDao.create(RestaurantConstants.RESTAURANT_IDS[0], CategoryConstants.CATEGORY_NAME);
        em.flush();

        assertNotNull(category);
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.getRestaurantId());
        assertEquals(CategoryConstants.CATEGORY_NAME, category.getName());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + category.getCategoryId() + " AND name = '" + category.getName() + "' AND restaurant_id = " + category.getRestaurantId()));
    }

    @Test
    public void testGetCategoryById() {
        final Optional<Category> category = categoryDao.getById(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0]);

        assertTrue(category.isPresent());
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0], category.get().getCategoryId().longValue());
        assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[1], category.get().getRestaurantId());
        assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_1[0], category.get().getOrderNum());
        assertFalse(category.get().getDeleted());
    }

    @Test
    public void testGetDeletedCategoryById() {
        final Optional<Category> category = categoryDao.getById(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2]);

        assertTrue(category.isPresent());
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2], category.get().getCategoryId().longValue());
        assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.get().getRestaurantId());
        assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[2], category.get().getOrderNum());
        assertTrue(category.get().getDeleted());
    }

    @Test
    public void testFindByRestaurantId() {
        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(RestaurantConstants.RESTAURANT_IDS[1]);

        assertNotNull(categories);
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1.length, categories.size());
        for (int i = 0; i < CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1.length; i++) {
            assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[i], categories.get(i).getCategoryId().longValue());
            assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_1[i], categories.get(i).getOrderNum());
            assertEquals(CategoryConstants.CATEGORY_NAME, categories.get(i).getName());
            assertEquals(RestaurantConstants.RESTAURANT_IDS[1], categories.get(i).getRestaurantId());
        }
    }

    @Test
    public void testFindByRestaurantIdWithDeletedCategory() {
        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(RestaurantConstants.RESTAURANT_IDS[0]);

        assertNotNull(categories);
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0.length - 1, categories.size());
        for (int i = 0; i < CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0.length - 1; i++) {
            assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[i], categories.get(i).getCategoryId().longValue());
            assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[i], categories.get(i).getOrderNum());
            assertEquals(CategoryConstants.CATEGORY_NAME, categories.get(i).getName());
            assertEquals(RestaurantConstants.RESTAURANT_IDS[0], categories.get(i).getRestaurantId());
        }
    }

    @Test
    public void testFindByRestaurantIdWithNoCategories() {
        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(RestaurantConstants.RESTAURANT_IDS[2]);

        assertNotNull(categories);
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_2.length, categories.size());
    }

    @Test
    public void testFindByRestaurantAndOrderNum() {
        final Optional<Category> category = categoryDao.getByRestaurantAndOrderNum(RestaurantConstants.RESTAURANT_IDS[0], CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[0]);

        assertTrue(category.isPresent());
        assertFalse(category.get().getDeleted());
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0], category.get().getCategoryId().longValue());
        assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.get().getRestaurantId());
        assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[0], category.get().getOrderNum());
    }

    @Test
    public void testFindDeletedCategoryByRestaurantAndOrderNum() {
        final Optional<Category> category = categoryDao.getByRestaurantAndOrderNum(RestaurantConstants.RESTAURANT_IDS[0], CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[2]);

        assertTrue(category.isPresent());
        assertTrue(category.get().getDeleted());
        assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2], category.get().getCategoryId().longValue());
        assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.get().getRestaurantId());
        assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[2], category.get().getOrderNum());
    }

    @Test(expected = CategoryNotFoundException.class)
    public void testDeleteNonExistingCategory() {
        categoryDao.delete(NON_EXISTING_CATEGORY_ID);
    }

    @Test(expected = CategoryDeletedException.class)
    public void testDeleteDeletedCategory() {
        categoryDao.delete(CategoryConstants.DELETED_CATEGORY_ID);
    }

    @Test
    @Rollback
    public void testDeleteCategory() {
        categoryDao.delete(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        em.flush();
        assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND deleted = true"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND deleted = false"));
        assertEquals(CategoryConstants.TOTAL_COUNT - 2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0]));
    }

    @Test
    @Rollback
    public void testMoveProductOneCategoryDown() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        long oldCategoryId = product.getCategoryId();
        categoryDao.moveProduct(product.getProductId(), CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[1]);
        em.flush();

        assertNotEquals(oldCategoryId, CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[1]);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[1]));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + oldCategoryId));
    }

    @Test
    @Rollback
    public void testMoveProductTwoCategoriesDown() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        long oldCategoryId = product.getCategoryId();
        categoryDao.moveProduct(product.getProductId(), CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2]);
        em.flush();

        assertNotEquals(oldCategoryId, CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2]);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2]));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + oldCategoryId));
    }

    @Test
    @Rollback
    public void testMoveProductNoCategories() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        categoryDao.moveProduct(product.getProductId(), CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        em.flush();

        assertEquals(product.getCategory().getCategoryId().longValue(), CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0] + " AND category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]));
    }

    @Test
    @Rollback
    public void testMoveProductOneCategoryUp() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        product.setCategoryId(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[1]);
        em.flush();

        long oldCategoryId = product.getCategoryId();
        categoryDao.moveProduct(product.getProductId(), CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        em.flush();

        assertNotEquals(oldCategoryId, CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + oldCategoryId));
    }

    @Test
    @Rollback
    public void testMoveProductTwoCategoriesUp() {
        final Product product = em.find(Product.class, ProductConstants.PRODUCT_FROM_CATEGORY_RESTAURANT_0[0]);
        product.setCategoryId(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2]);
        em.flush();

        long oldCategoryId = product.getCategoryId();
        categoryDao.moveProduct(product.getProductId(), CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        em.flush();

        assertNotEquals(oldCategoryId, CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "products", "product_id = " + product.getProductId() + " AND category_id = " + oldCategoryId));
    }
}
