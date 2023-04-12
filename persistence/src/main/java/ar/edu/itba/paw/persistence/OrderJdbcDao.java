package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.persistance.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrderJdbcDao implements OrderDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<Order> orderRowMapper = (ResultSet rs, int rowNum) -> new Order(
            rs.getLong("order_id"),
            rs.getLong("order_type_id"),
            rs.getLong("restaurant_id"),
            rs.getLong("user_id"),
            rs.getTimestamp("order_date").toLocalDateTime(),
            rs.getString("address"),
            rs.getInt("table_number")
    );

    @Autowired
    public OrderJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("orders")
                .usingGeneratedKeyColumns("order_id");
    }


    @Override
    public Order create(long orderTypeId, long restaurantId, long userId) {
        final Map<String, Object> orderData = new HashMap<>();
        orderData.put("order_type_id", orderTypeId);
        orderData.put("restaurant_id", restaurantId);
        orderData.put("user_id", userId);
        final int orderId = jdbcInsert.execute(orderData);

        return new Order(orderId, orderTypeId, restaurantId, userId);
    }

    @Override
    public Optional<Order> findOrderById(long orderId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE order_id = ?", orderRowMapper, orderId).stream().findFirst();
    }

    @Override
    public List<Order> findOrdersByUserId(long userId, long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE user_id = ? AND restaurant_id = ?", orderRowMapper, userId, restaurantId);
    }

    @Override
    public List<Order> findOrdersByRestaurantId(long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE restaurant_id = ?", orderRowMapper, restaurantId);
    }

    @Override
    public List<Order> findByOrdersTypeId(long orderTypeId, long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE order_type_id = ? AND restaurant_id = ?", orderRowMapper, orderTypeId, restaurantId);
    }

    @Override
    public List<Order> findByOrdersBetweenDates(LocalDateTime start, LocalDateTime end, long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE order_date BETWEEN ? AND ? AND restaurant_id = ?", orderRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end), restaurantId);
    }

    @Override
    public List<Order> findByOrdersAddress(String address, long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE address = ? AND restaurant_id = ?", orderRowMapper, address, restaurantId);
    }

    @Override
    public List<Order> findByOrdersTableNumber(int tableNumber, long restaurantId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE table_number = ? AND restaurant_id = ?", orderRowMapper, tableNumber, restaurantId);
    }

    @Override
    public boolean updateAddress(long orderId, String address) {
        return jdbcTemplate.update("UPDATE orders SET address = ? WHERE order_id = ?", address, orderId) > 0;
    }

    @Override
    public boolean updateTableNumber(long orderId, int tableNumber) {
        return jdbcTemplate.update("UPDATE orders SET table_number = ? WHERE order_id = ?", tableNumber, orderId) > 0;
    }

    @Override
    public boolean delete(long orderId) {
        return jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderRowMapper, orderId) > 0;
    }
}
