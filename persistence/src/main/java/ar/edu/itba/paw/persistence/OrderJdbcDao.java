package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderJdbcDao {

    static final String IS_IN_PROGRESS_COND = "(date_delivered IS NULL AND date_cancelled IS NULL)";
    static final String SELECT_ITEMLESS_ORDERS = "SELECT " + TableFields.ORDERS_FIELDS + ", " + TableFields.RESTAURANTS_FIELDS + ", " + TableFields.USERS_FIELDS +
            ", COUNT(*) AS order_item_count, SUM(order_items.quantity*products.price) AS order_price" +
            " FROM orders JOIN restaurants ON orders.restaurant_id = restaurants.restaurant_id" +
            " JOIN users on orders.user_id = users.user_id" +
            " LEFT OUTER JOIN order_items ON orders.order_id = order_items.order_id" +
            " LEFT OUTER JOIN products ON order_items.product_id = products.product_id" +
            " GROUP BY orders.order_id, restaurants.restaurant_id, users.user_id";
}
