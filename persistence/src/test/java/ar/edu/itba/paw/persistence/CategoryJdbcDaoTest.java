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
    private static final long CATEGORY_ID = 12421;
    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final long RESTAURANT_ID = 79874;
    private static final String CATEGORY_NAME = "Postgres Dulces";
    private static final String RESTAURANT_NAME = "Cafe Mataderos";
    private static final String RESTAURANT_EMAIL = "pedro@frompedros.com";
    private static final int MAX_TABLES = 20;
    private static final int ORDER_NUM = 1293;
    private static final int SPECIALTY = 1;
    private static final String[] categoryNames = {"Category 1", "Category 2", "Category 3", "Category 4"};

    @Autowired
    private DataSource ds;

    @Autowired
    private CategoryJdbcDao categoryDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "categories", "restaurants", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "', " + MAX_TABLES + ", " + SPECIALTY + ", " + USER_ID + ")");
    }

    /* FIXME update to match implementation
    @Test
    public void testCreate() throws SQLException {
        final Category category = categoryDao.create(RESTAURANT_ID, CATEGORY_NAME, ORDER_NUM);

        Assert.assertNotNull(category);
        Assert.assertEquals(CATEGORY_NAME, category.getName());
        Assert.assertEquals(ORDER_NUM, category.getOrder());
        Assert.assertEquals(RESTAURANT_ID, category.getRestaurant().getRestaurantId());
    }*/

    @Test
    public void testGetCategoryById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num) VALUES (" + CATEGORY_ID + ", '" + CATEGORY_NAME + "', " + RESTAURANT_ID + ", " + ORDER_NUM + ")");
        final Optional<Category> category = categoryDao.getById(CATEGORY_ID);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CATEGORY_ID, category.get().getCategoryId());
        Assert.assertEquals(CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(ORDER_NUM, category.get().getOrder());
        Assert.assertEquals(RESTAURANT_ID, category.get().getRestaurant().getRestaurantId());
    }

    @Test
    public void testFindByRestaurantId() throws SQLException {
        for (int i = categoryNames.length; i > 0 ; i--)
            jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num) VALUES (" + i + ", '" + categoryNames[i - 1] + "', " + RESTAURANT_ID + ", " + i + ")");

        final List<Category> category = categoryDao.getByRestaurantSortedByOrder(RESTAURANT_ID);

        Assert.assertNotNull(category);
        Assert.assertEquals(categoryNames.length, category.size());
        for (int i = 0; i < categoryNames.length; i++) {
            Assert.assertEquals(i + 1, category.get(i).getOrder());
            Assert.assertEquals(i + 1, category.get(i).getCategoryId());
            Assert.assertEquals(categoryNames[i], category.get(i).getName());
            Assert.assertEquals(RESTAURANT_ID, category.get(i).getRestaurant().getRestaurantId());
        }
    }

    @Test
    public void testUpdateCategoryName() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "'," + ORDER_NUM + ")");
        Assert.assertTrue(categoryDao.updateName(CATEGORY_ID, CATEGORY_NAME + "blabla"));
    }

    @Test
    public void testUpdateCategoryOrder() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "'," + ORDER_NUM + ")");

        final int newOrder = ORDER_NUM + 1;
        Assert.assertTrue(categoryDao.updateOrder(CATEGORY_ID, newOrder));
    }

    @Test
    public void testDeleteCategory() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "'," + ORDER_NUM + ")");

        Optional<Category> category1 = categoryDao.getById(CATEGORY_ID);
        Assert.assertTrue(category1.isPresent());
        Assert.assertTrue(categoryDao.delete(CATEGORY_ID));
    }
}
