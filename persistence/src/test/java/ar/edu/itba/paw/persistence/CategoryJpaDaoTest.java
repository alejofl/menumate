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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CategoryJpaDaoTest {

    private static final long USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final String PREFERRED_LANGUAGE = "qx";
    private static final boolean IS_ACTIVE = true;
    private static final long RESTAURANT_ID = 79874;
    private static final long CATEGORY_ID = 12421L;
    private static final long CATEGORY_ID_2 = 1111L;
    private static final boolean CATEGORY_DELETED = false;
    private static final String CATEGORY_NAME = "Postgres Dulces";
    private static final String CATEGORY_NAME_2 = "Postgres Salados";
    private static final String RESTAURANT_NAME = "Cafe Mataderos";
    private static final String RESTAURANT_EMAIL = "pedro@frompedros.com";
    private static final String RESTAURANT_ADDRESS = "Av. Juan B. Justo 1234";
    private static final String RESTAURANT_DESCRIPTION = "Cafe de Mataderos";
    private static final boolean RESTAURANT_DELETED = false;
    private static final boolean RESTAURANT_IS_ACTIVE = true;
    private static final Timestamp RESTAURANT_CREATION_DATE = new Timestamp(123456789);
    private static final int MAX_TABLES = 20;
    private static final int ORDER_NUM = 1293;
    private static final int ORDER_NUM_2 = 5000;

    private static final int SPECIALTY = 1;
    private static final String[] categoryNames = {"Category 1", "Category 2", "Category 3", "Category 4"};

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "categories", "restaurants", "users");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name, is_active, preferred_language) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "', " + IS_ACTIVE + ", '" + PREFERRED_LANGUAGE + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active) VALUES (" + RESTAURANT_ID + ", '" + RESTAURANT_NAME + "', '" + RESTAURANT_EMAIL + "', " + MAX_TABLES + ", " + SPECIALTY + ", " + USER_ID + ", '" + RESTAURANT_ADDRESS + "', '" + RESTAURANT_DESCRIPTION + "', '" + RESTAURANT_CREATION_DATE + "', " + RESTAURANT_DELETED + ", " + RESTAURANT_IS_ACTIVE + ")");
    }

    @Test
    public void testCreate() throws SQLException {
        final Category category = categoryDao.create(RESTAURANT_ID, CATEGORY_NAME);
        em.flush();

        Assert.assertNotNull(category);
        Assert.assertEquals(CATEGORY_NAME, category.getName());
        Assert.assertEquals(RESTAURANT_ID, category.getRestaurantId());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + category.getCategoryId() + " AND name = '" + CATEGORY_NAME + "' AND restaurant_id = " + RESTAURANT_ID));
    }

    @Test
    public void testGetCategoryById() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num, deleted) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "', " + ORDER_NUM + ", " + CATEGORY_DELETED + ")");
        final Optional<Category> category = categoryDao.getById(CATEGORY_ID);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CATEGORY_ID, category.get().getCategoryId().intValue());
        Assert.assertEquals(CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(RESTAURANT_ID, category.get().getRestaurantId());
        Assert.assertEquals(ORDER_NUM, category.get().getOrderNum());
    }

    @Test
    public void testFindByRestaurantId() throws SQLException {
        for (int i = categoryNames.length; i > 0; i--)
            jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num, deleted) VALUES (" + i + ", '" + categoryNames[i - 1] + "', " + RESTAURANT_ID + ", " + i + ", " + CATEGORY_DELETED + ")");

        final List<Category> category = categoryDao.getByRestaurantSortedByOrder(RESTAURANT_ID);

        Assert.assertNotNull(category);
        Assert.assertEquals(categoryNames.length, category.size());
        for (int i = 0; i < categoryNames.length; i++) {
            Assert.assertEquals(i + 1, category.get(i).getCategoryId().intValue());
            Assert.assertEquals(i + 1, category.get(i).getOrderNum());
            Assert.assertEquals(categoryNames[i], category.get(i).getName());
            Assert.assertEquals(RESTAURANT_ID, category.get(i).getRestaurantId());
        }
    }

    @Test
    public void testFindByRestaurantAndOrderNum() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id, name, restaurant_id, order_num, deleted) VALUES (" + CATEGORY_ID + ", '" + CATEGORY_NAME + "', " + RESTAURANT_ID + ", " + ORDER_NUM + ", " + CATEGORY_DELETED + ")");
        final Optional<Category> category = categoryDao.getByRestaurantAndOrderNum(RESTAURANT_ID, ORDER_NUM);

        Assert.assertTrue(category.isPresent());
        Assert.assertEquals(CATEGORY_ID, category.get().getCategoryId().intValue());
        Assert.assertEquals(CATEGORY_NAME, category.get().getName());
        Assert.assertEquals(RESTAURANT_ID, category.get().getRestaurantId());
        Assert.assertEquals(ORDER_NUM, category.get().getOrderNum());
    }

    @Test
    public void testDeleteCategory() throws SQLException {
        jdbcTemplate.execute("INSERT INTO categories (category_id ,restaurant_id, name, order_num, deleted) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "'," + ORDER_NUM + ", " + false + ")");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + CATEGORY_ID + " AND name = '" + CATEGORY_NAME + "' AND restaurant_id = " + RESTAURANT_ID + " AND order_num = " + ORDER_NUM + " AND deleted = " + false));
        categoryDao.delete(CATEGORY_ID);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + CATEGORY_ID + " AND name = '" + CATEGORY_NAME + "' AND restaurant_id = " + RESTAURANT_ID + " AND order_num = " + ORDER_NUM + " AND deleted = " + true));
    }

    // FIXME: This test is not working due to BEGIN and COMMIT statements in the DAO
//    @Test
//    public void testSwapOrder() throws SQLException {
//        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num, deleted) VALUES (" + CATEGORY_ID + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME + "', " + ORDER_NUM + ", " + CATEGORY_DELETED + ")");
//        jdbcTemplate.execute("INSERT INTO categories (category_id, restaurant_id, name, order_num, deleted) VALUES (" + CATEGORY_ID_2 + ", " + RESTAURANT_ID + ", '" + CATEGORY_NAME_2 + "', " + ORDER_NUM_2 + ", " + CATEGORY_DELETED + ")");
//        categoryDao.swapOrder(RESTAURANT_ID, ORDER_NUM, ORDER_NUM_2);
//        em.flush();
//        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + CATEGORY_ID + " AND name = '" + CATEGORY_NAME + "' AND restaurant_id = " + RESTAURANT_ID + " AND order_num = " + ORDER_NUM_2 + " AND deleted = " + CATEGORY_DELETED));
//        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "categories", "category_id = " + CATEGORY_ID_2 + " AND name = '" + CATEGORY_NAME_2 + "' AND restaurant_id = " + RESTAURANT_ID + " AND order_num = " + ORDER_NUM + " AND deleted = " + CATEGORY_DELETED));
//    }
}
