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
            rs.getInt("user_id"),
            rs.getString("user_username"),
            rs.getString("user_password"),
            rs.getString("user_name"),
            rs.getString("user_email")
    );

    static final RowMapper<byte[]> IMAGE_ROW_MAPPER = (ResultSet rs, int rowNum) -> rs.getBytes("bytes");


    static final RowMapper<Restaurant> RESTAURANT_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Restaurant(
            rs.getInt("restaurant_id"),
            rs.getString("restaurant_name"),
            rs.getString("restaurant_email"),
            rs.getInt("restaurant_logo_id"),
            rs.getInt("restaurant_portrait_1_id"),
            rs.getInt("restaurant_portrait_2_id"),
            rs.getString("restaurant_address"),
            rs.getString("restaurant_description"),
            rs.getBoolean("restaurant_is_active")
    );
    static final RowMapper<Category> CATEGORY_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Category(
            rs.getInt("category_id"),
            RESTAURANT_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getString("category_name"),
            rs.getInt("category_order")
    );

    static final RowMapper<Product> PRODUCT_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Product(
            rs.getInt("product_id"),
            CATEGORY_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getString("product_name"),
            rs.getDouble("product_price"),
            rs.getString("product_description"),
            rs.getInt("product_image_id"),
            rs.getBoolean("product_available")
    );

    static final RowMapper<OrderItem> ORDER_ITEM_ROW_MAPPER = (ResultSet rs, int rowNum) -> new OrderItem(
            PRODUCT_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getInt("order_item_line_number"),
            rs.getInt("order_item_quantity"),
            rs.getString("order_item_comment")
    );
}
