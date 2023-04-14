package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

class RowMappers {

    static final RowMapper<User> USER_ROW_MAPPER = (ResultSet rs, int rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("user_username"),
            rs.getString("user_password"),
            rs.getString("user_email")
    );

    static final RowMapper<byte[]> IMAGE_ROW_MAPPER = (ResultSet rs, int rowNum) -> rs.getBytes("bytes");

    static final RowMapper<Restaurant> RESTAURANT_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Restaurant(
            rs.getLong("restaurant_id"),
            rs.getString("restaurant_name"),
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

    static final RowMapper<OrderType> ORDER_TYPE_ROW_MAPPER = (ResultSet rs, int rowNum) -> new OrderType(
            rs.getLong("order_type_id"),
            rs.getString("order_type_name")
    );

    static final RowMapper<Order> ORDER_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Order(
            rs.getLong("order_id"),
            ORDER_TYPE_ROW_MAPPER.mapRow(rs, rowNum),
            RESTAURANT_ROW_MAPPER.mapRow(rs, rowNum),
            USER_ROW_MAPPER.mapRow(rs, rowNum),
            rs.getTimestamp("order_date").toLocalDateTime(),
            rs.getString("order_address"),
            rs.getInt("order_table_number")
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
}
