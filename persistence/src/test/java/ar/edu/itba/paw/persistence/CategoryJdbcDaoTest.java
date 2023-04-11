package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
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
public class CategoryJdbcDaoTest {


    @Autowired
    private DataSource ds;

    @Autowired
    private CategoryJdbcDao categoryDao;

    @Autowired
    private RestaurantJdbcDao restaurantDao;

    private JdbcTemplate jdbcTemplate;
    private static final long CATEGORY_ID = 1;
    private static final long RESTAURANT_ID = 1;
    private static final String CATEGORY_NAME = "CATEGORY_NAME";
    private static final String RESTAURANT_NAME = "RESTAURANT_NAME";
    private static final long ORDER = 1;
    private static final String[] categoryNames = {"Category 1", "Category 2", "Category 3", "Category 4"};

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"categories", "restaurants");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name) VALUES (" + RESTAURANT_ID +", '" + RESTAURANT_NAME + "')");
    }

    @Test
    public void testCreate() throws SQLException {

        final Category category = categoryDao.createCategory(RESTAURANT_ID, CATEGORY_NAME, ORDER);

        Assert.assertNotNull(category);
        Assert.assertEquals(CATEGORY_ID, category.getCategoryId());
        Assert.assertEquals(CATEGORY_NAME, category.getName());
        Assert.assertEquals(ORDER, category.getOrder());
        Assert.assertEquals(RESTAURANT_ID, category.getRestaurantId());
    }

    @Test
    public void testGetCategoryById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num) VALUES (" + CATEGORY_ID +", '" + CATEGORY_NAME + "', " + RESTAURANT_ID + ", " + ORDER + ")");
        final Optional<Category> category = categoryDao.getCategoryById(CATEGORY_ID);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CATEGORY_ID, category.get().getCategoryId());
        Assert.assertEquals(CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(ORDER, category.get().getOrder());
        Assert.assertEquals(RESTAURANT_ID, category.get().getRestaurantId());
    }

    @Test
    public void testFindByRestaurantId() throws SQLException {

        for (int i = 1; i < categoryNames.length + 1; i++) {
            jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num) VALUES (" + i +", '" + categoryNames[i-1] + "', " + RESTAURANT_ID + ", " + i + ")");
        }

        final List<Category> category = categoryDao.findByRestaurantId(RESTAURANT_ID);

        Assert.assertNotNull(category);
        Assert.assertEquals(categoryNames.length, category.size());
        for (int i = 0; i < categoryNames.length; i++) {
            Assert.assertEquals(i + 1, category.get(i).getOrder());
            Assert.assertEquals(i + 1, category.get(i).getCategoryId());
            Assert.assertEquals(categoryNames[i], category.get(i).getName());
            Assert.assertEquals(RESTAURANT_ID, category.get(i).getRestaurantId());
        }
    }

    @Test
    public void testUpdateCategoryName() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num) VALUES (" + CATEGORY_ID +", " + RESTAURANT_ID +", '" + CATEGORY_NAME +"',"+ ORDER + ")");

        final String newName = CATEGORY_NAME + "blabla";

        categoryDao.updateName(CATEGORY_ID, newName);

        final Optional<Category> category = categoryDao.getCategoryById(CATEGORY_ID);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CATEGORY_ID, category.get().getCategoryId());
        Assert.assertEquals(RESTAURANT_ID, category.get().getRestaurantId());
        Assert.assertEquals(ORDER, category.get().getOrder());
        Assert.assertNotEquals(CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(newName, category.get().getName());
    }

    @Test
    public void testUpdateCategoryOrder() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num) VALUES (" + CATEGORY_ID +", " + RESTAURANT_ID +", '" + CATEGORY_NAME +"',"+ ORDER + ")");

        final long newOrder = ORDER + 1;

        categoryDao.updateOrder(CATEGORY_ID, newOrder);

        final Optional<Category> category = categoryDao.getCategoryById(CATEGORY_ID);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CATEGORY_ID, category.get().getCategoryId());
        Assert.assertEquals(RESTAURANT_ID, category.get().getRestaurantId());
        Assert.assertEquals(CATEGORY_NAME, category.get().getName());
        Assert.assertNotEquals(ORDER, category.get().getOrder());
        Assert.assertEquals(newOrder, category.get().getOrder());
    }

    @Test
    public void testDeleteCategory() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num) VALUES (" + CATEGORY_ID +", " + RESTAURANT_ID +", '" + CATEGORY_NAME +"',"+ ORDER + ")");
        Optional<Category> category1 = categoryDao.getCategoryById(CATEGORY_ID);
        Assert.assertTrue(category1.isPresent());

        categoryDao.deleteCategory(CATEGORY_ID);

        category1 = categoryDao.getCategoryById(CATEGORY_ID);
        Assert.assertFalse(category1.isPresent());
    }

}
