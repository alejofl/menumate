package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

class RowMappers {

    static LocalDateTime timestampToLocalDateTimeOrNull(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    static final RowMapper<User> USER_ROW_MAPPER = (ResultSet rs, int rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("user_username"),
            rs.getString("user_password"),
            rs.getString("user_name"),
            rs.getString("user_email")
    );

    static final RowMapper<byte[]> IMAGE_ROW_MAPPER = (ResultSet rs, int rowNum) -> rs.getBytes("bytes");

    static final RowMapper<Restaurant> RESTAURANT_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Restaurant(
            rs.getLong("restaurant_id"),
            rs.getString("restaurant_name"),
            rs.getString("restaurant_email"),
            rs.getLong("restaurant_logo_id"),
            rs.getLong("restaurant_portrait_1_id"),
            rs.getLong("restaurant_portrait_2_id"),
            rs.getString("restaurant_address"),
            rs.getString("restaurant_description"),
            rs.getBoolean("restaurant_is_active")
    );
    static final RowMapper<Category> CATEGORY_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Category(
            rs.getLong("category_id"),
            RESTAURANT_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getString("category_name"),
            rs.getLong("category_order")
    );

    static final RowMapper<Product> PRODUCT_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Product(
            rs.getLong("product_id"),
            CATEGORY_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getString("product_name"),
            rs.getDouble("product_price"),
            rs.getString("product_description"),
            rs.getLong("product_image_id"),
            rs.getBoolean("product_available")
    );

    static final RowMapper<OrderItem> ORDER_ITEM_ROW_MAPPER = (ResultSet rs, int rowNum) -> new OrderItem(
            PRODUCT_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getInt("order_item_line_number"),
            rs.getInt("order_item_quantity"),
            rs.getString("order_item_comment")
    );
}
