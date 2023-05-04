package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Extractors {

    static final ResultSetExtractor<List<Order>> ORDER_EXTRACTOR = (ResultSet rs) -> {
        List<Order> orders = new ArrayList<>();

        RowMapper<Restaurant> restaurantRowMapper = ReusingRowMappers.getRestaurantRowMapper();
        RowMapper<User> userRowMapper = ReusingRowMappers.getUserRowMapper();
        RowMapper<OrderItem> orderItemRowMapper = ReusingRowMappers.getOrderItemRowMapper();

        boolean isFirst = true;

        int orderId = 0;
        OrderType orderType = null;
        Restaurant restaurant = null;
        User user = null;
        LocalDateTime dateOrdered = null;
        LocalDateTime dateConfirmed = null;
        LocalDateTime dateReady = null;
        LocalDateTime dateDelivered = null;
        LocalDateTime dateCancelled = null;
        String address = null;
        int tableNumber = 0;
        List<OrderItem> items = null;

        while (rs.next()) {
            int currentOrderId = rs.getInt("order_id");
            if (isFirst || orderId != currentOrderId) {
                if (!isFirst) {
                    orders.add(new Order(orderId, orderType, restaurant, user, dateOrdered, dateConfirmed, dateReady,
                            dateDelivered, dateCancelled, address, tableNumber, Collections.unmodifiableList(items)));
                }

                orderId = currentOrderId;
                orderType = OrderType.values()[rs.getInt("order_type")];
                restaurant = restaurantRowMapper.mapRow(rs, 1);
                user = userRowMapper.mapRow(rs, 1);
                dateOrdered = SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_ordered"));
                dateConfirmed = SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_confirmed"));
                dateReady = SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_ready"));
                dateDelivered = SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_delivered"));
                dateCancelled = SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_cancelled"));
                address = rs.getString("order_address");
                tableNumber = rs.getInt("order_table_number");

                items = new ArrayList<>();
                isFirst = false;
            }

            rs.getInt("product_id");
            if (!rs.wasNull())
                items.add(orderItemRowMapper.mapRow(rs, 1));
        }

        if (!isFirst) {
            orders.add(new Order(orderId, orderType, restaurant, user, dateOrdered, dateConfirmed, dateReady,
                    dateDelivered, dateCancelled, address, tableNumber, Collections.unmodifiableList(items)));
        }

        return orders;
    };
}
