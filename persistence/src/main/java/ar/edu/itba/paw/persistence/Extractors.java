package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Extractors {

    public static final ResultSetExtractor<List<Order>> ORDER_EXTRACTOR = (ResultSet rs) -> {
        List<Order> orders = new ArrayList<>();

        boolean isFirst = true;

        long orderId = 0;
        OrderType orderType = null;
        Restaurant restaurant = null;
        User user = null;
        LocalDateTime dateOrdered = null;
        LocalDateTime dateDelivered = null;
        String address = null;
        int tableNumber = 0;
        List<OrderItem> items = null;

        while (rs.next()) {
            long currentOrderId = rs.getLong("order_id");
            if (isFirst || orderId != currentOrderId) {
                if (!isFirst) {
                    orders.add(new Order(orderId, orderType, restaurant, user, dateOrdered, dateDelivered, address,
                            tableNumber, Collections.unmodifiableList(items)));
                }

                orderId = currentOrderId;
                orderType = RowMappers.ORDER_TYPE_ROW_MAPPER.mapRow(rs, 1);
                restaurant = RowMappers.RESTAURANT_ROW_MAPPER.mapRow(rs, 1);
                user = RowMappers.USER_ROW_MAPPER.mapRow(rs, 1);
                dateOrdered = RowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_ordered"));
                dateDelivered = RowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_delivered"));
                address = rs.getString("order_address");
                tableNumber = rs.getInt("order_table_number");

                items = new ArrayList<>();
                isFirst = false;
            }

            if (!rs.wasNull())
                items.add(RowMappers.ORDER_ITEM_ROW_MAPPER.mapRow(rs, 1));
        }

        if (!isFirst) {
            orders.add(new Order(orderId, orderType, restaurant, user, dateOrdered, dateDelivered, address,
                    tableNumber, Collections.unmodifiableList(items)));
        }

        return orders;
    };
}
