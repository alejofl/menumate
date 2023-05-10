package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReviewJdbcDaoTest {
    private static final int USER_ID = 791;
    private static final String USER_EMAIL = "peter@peter.com";
    private static final String USER_PASSWORD = "super12secret34";
    private static final String USER_NAME = "Peter Parker";
    private static final int OWNER_ID = 313;
    private static final String OWNER_EMAIL = "pedro@pedro.com";
    private static final String OWNER_PASSWORD = "mega12secreto34";
    private static final String OWNER_NAME = "Pedro Parker";
    private static final int RESTAURANT_ID1 = 5123;
    private static final String RESTAURANT_NAME1 = "pedros";
    private static final String RESTAURANT_EMAIL1 = "pedros@frompedros.com";
    private static final int RESTAURANT_ID2 = 4242;
    private static final String RESTAURANT_NAME2 = "La Mejor Pizza";
    private static final String RESTAURANT_EMAIL2 = "pizzeria@pizza.com";
    private static final int MAX_TABLES = 20;
    private static final int USER_ID_NONE = 1234;
    private static final int RESTAURANT_ID_NONE = 1234;
    private static final int ORDER_ID1 = 8844;
    private static final int ORDER_ID2 = 9090;
    private static final int RATING1 = 1;
    private static final int RATING2 = 4;
    private static final OrderType ORDER_TYPE = OrderType.TAKEAWAY;

    @Autowired
    private DataSource ds;

    @Autowired
    private ReviewJdbcDao reviewDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "restaurants", "restaurant_roles");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + USER_ID + ", '" + USER_EMAIL + "', '" + USER_PASSWORD + "', '" + USER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, password, name) VALUES (" + OWNER_ID + ", '" + OWNER_EMAIL + "', '" + OWNER_PASSWORD + "', '" + OWNER_NAME + "')");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables) VALUES (" + RESTAURANT_ID1 + ", '" + RESTAURANT_NAME1 + "', '" + RESTAURANT_EMAIL1 + "', " + OWNER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO restaurants (restaurant_id, name, email, owner_user_id, max_tables) VALUES (" + RESTAURANT_ID2 + ", '" + RESTAURANT_NAME2 + "', '" + RESTAURANT_EMAIL2 + "', " + OWNER_ID + ", " + MAX_TABLES + ")");
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES ("+ ORDER_ID1 +", "+ORDER_TYPE.ordinal()+", "+RESTAURANT_ID1+", "+USER_ID+", now(), now(), now(), now())");
        jdbcTemplate.execute("INSERT INTO orders (order_id, order_type, restaurant_id, user_id, date_ordered, date_delivered, date_confirmed, date_ready) VALUES ("+ ORDER_ID2 +", "+ORDER_TYPE.ordinal()+", "+RESTAURANT_ID2+", "+USER_ID+", now(), now(), now(), now())");
    }
}
