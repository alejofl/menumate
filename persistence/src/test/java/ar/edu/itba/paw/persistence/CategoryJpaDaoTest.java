package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.CategoryConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
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
import java.util.List;
import java.util.Optional;

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

        Assert.assertNotNull(category);
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.getRestaurantId());
        Assert.assertEquals(CategoryConstants.CATEGORY_NAME, category.getName());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + category.getCategoryId() + " AND name = '" + category.getName() + "' AND restaurant_id = " + category.getRestaurantId()));
    }

    @Test
    public void testGetCategoryById() {
        final Optional<Category> category = categoryDao.getById(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0]);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[0], category.get().getCategoryId().longValue());
        Assert.assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[1], category.get().getRestaurantId());
        Assert.assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_1[0], category.get().getOrderNum());
        Assert.assertFalse(category.get().getDeleted());
    }

    @Test
    public void testGetDeletedCategoryById() {
        final Optional<Category> category = categoryDao.getById(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2]);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2], category.get().getCategoryId().longValue());
        Assert.assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.get().getRestaurantId());
        Assert.assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[2], category.get().getOrderNum());
        Assert.assertTrue(category.get().getDeleted());
    }

    @Test
    public void testFindByRestaurantId() {
        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(RestaurantConstants.RESTAURANT_IDS[1]);

        Assert.assertNotNull(categories);
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1.length, categories.size());
        for (int i=0; i<CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1.length; i++) {
            Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_1[i], categories.get(i).getCategoryId().longValue());
            Assert.assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_1[i], categories.get(i).getOrderNum());
            Assert.assertEquals(CategoryConstants.CATEGORY_NAME, categories.get(i).getName());
            Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[1], categories.get(i).getRestaurantId());
        }
    }

    @Test
    public void testFindByRestaurantIdWithDeletedCategory() {
        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(RestaurantConstants.RESTAURANT_IDS[0]);

        Assert.assertNotNull(categories);
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0.length - 1, categories.size());
        for (int i=0; i<CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0.length - 1; i++) {
            Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[i], categories.get(i).getCategoryId().longValue());
            Assert.assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[i], categories.get(i).getOrderNum());
            Assert.assertEquals(CategoryConstants.CATEGORY_NAME, categories.get(i).getName());
            Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], categories.get(i).getRestaurantId());
        }
    }

    @Test
    public void testFindByRestaurantIdWithNoCategories() {

        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(RestaurantConstants.RESTAURANT_IDS[2]);

        Assert.assertNotNull(categories);
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_2.length, categories.size());
    }

    @Test
    public void testFindByRestaurantAndOrderNum() {
        final Optional<Category> category = categoryDao.getByRestaurantAndOrderNum(RestaurantConstants.RESTAURANT_IDS[0], CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[0]);

        Assert.assertTrue(category.isPresent());
        Assert.assertFalse(category.get().getDeleted());
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0], category.get().getCategoryId().longValue());
        Assert.assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.get().getRestaurantId());
        Assert.assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[0], category.get().getOrderNum());
    }

    @Test
    public void testFindDeletedCategoryByRestaurantAndOrderNum() {
        final Optional<Category> category = categoryDao.getByRestaurantAndOrderNum(RestaurantConstants.RESTAURANT_IDS[0], CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[2]);

        Assert.assertTrue(category.isPresent());
        Assert.assertTrue(category.get().getDeleted());
        Assert.assertEquals(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[2], category.get().getCategoryId().longValue());
        Assert.assertEquals(CategoryConstants.CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(RestaurantConstants.RESTAURANT_IDS[0], category.get().getRestaurantId());
        Assert.assertEquals(CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[2], category.get().getOrderNum());
    }

    @Test(expected = CategoryNotFoundException.class)
    public void testDeleteNonExistingCategory() {
        categoryDao.delete(NON_EXISTING_CATEGORY_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteDeletedCategory() {
        categoryDao.delete(CategoryConstants.DELETED_CATEGORY_ID);
    }

    @Test
    @Rollback
    public void testDeleteCategory() {
        categoryDao.delete(CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0]);
        em.flush();
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND deleted = true"));
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND deleted = false"));
        Assert.assertEquals(CategoryConstants.TOTAL_COUNT - 2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0]));
    }

// NOTE: This currently can't be tested because swapOrder uses deferred constraints, which HSQLDB doesn't support.
//    @Test
//    @Rollback
//    public void testSwapOrder() {
//        categoryDao.swapOrder(RestaurantConstants.RESTAURANT_IDS[0], CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[0], CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[1]);
//        em.flush();
//
//        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[0] + " AND name = '" + CategoryConstants.CATEGORY_NAME + "' AND restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND order_num = " + CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[1]));
//        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + CategoryConstants.CATEGORY_IDS_FOR_RESTAURANT_0[1] + " AND name = '" + CategoryConstants.CATEGORY_NAME + "' AND restaurant_id = " + RestaurantConstants.RESTAURANT_IDS[0] + " AND order_num = " + CategoryConstants.CATEGORY_ORDER_FOR_RESTAURANT_0[0] ));
//    }
}
