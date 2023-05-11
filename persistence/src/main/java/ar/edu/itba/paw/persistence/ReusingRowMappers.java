package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.Triplet;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class ReusingRowMappers {
    private static abstract class ReusingRowMapper<K, T> implements RowMapper<T> {
        private final Map<K, T> map = new HashMap<>();

        @Override
        public T mapRow(ResultSet resultSet, int i) throws SQLException {
            K pk = getKey(resultSet, i);
            T value = map.get(pk);
            if (value == null) {
                value = mapObject(pk, resultSet);
                map.put(pk, value);
            }

            return value;
        }

        public abstract K getKey(ResultSet resultSet, int i) throws SQLException;

        public abstract T mapObject(K pk, ResultSet rs) throws SQLException;
    }

    private static abstract class LongKeyReusingRowMapper<T> extends ReusingRowMapper<Long, T> {
        private final String keyColumnName;

        public LongKeyReusingRowMapper(String keyColumnName) {
            this.keyColumnName = keyColumnName;
        }

        @Override
        public Long getKey(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getLong(keyColumnName);
        }
    }

    private static abstract class TripleLongKeyReusingRowMapper<T> extends ReusingRowMapper<Triplet<Long, Long, Long>, T> {
        private final String keyColumnName1;
        private final String keyColumnName2;
        private final String keyColumnName3;

        public TripleLongKeyReusingRowMapper(String keyColumnName1, String keyColumnName2, String keyColumnName3) {
            this.keyColumnName1 = keyColumnName1;
            this.keyColumnName2 = keyColumnName2;
            this.keyColumnName3 = keyColumnName3;
        }

        @Override
        public Triplet<Long, Long, Long> getKey(ResultSet resultSet, int i) throws SQLException {
            return new Triplet<>(
                    resultSet.getLong(keyColumnName1),
                    resultSet.getLong(keyColumnName2),
                    resultSet.getLong(keyColumnName3)
            );
        }
    }

    private static class UserReusingRowMapper extends LongKeyReusingRowMapper<User> {
        public UserReusingRowMapper() {
            super("user_id");
        }

        @Override
        public User mapObject(Long pk, ResultSet rs) throws SQLException {
            return new User(
                    pk,
                    rs.getString("user_email"),
                    rs.getString("user_name"),
                    SimpleRowMappers.readLongOrNull(rs, "user_image_id"),
                    rs.getBoolean("user_is_active")
            );
        }
    }

    static RowMapper<User> getUserRowMapper() {
        return new UserReusingRowMapper();
    }

    private static class RestaurantReusingRowMapper extends LongKeyReusingRowMapper<Restaurant> {
        public RestaurantReusingRowMapper() {
            super("restaurant_id");
        }

        @Override
        public Restaurant mapObject(Long pk, ResultSet rs) throws SQLException {
            return new Restaurant(
                    pk,
                    rs.getString("restaurant_name"),
                    rs.getString("restaurant_email"),
                    rs.getLong("restaurant_owner_user_id"),
                    SimpleRowMappers.readLongOrNull(rs, "restaurant_logo_id"),
                    SimpleRowMappers.readLongOrNull(rs, "restaurant_portrait_1_id"),
                    SimpleRowMappers.readLongOrNull(rs, "restaurant_portrait_2_id"),
                    rs.getString("restaurant_address"),
                    rs.getString("restaurant_description"),
                    rs.getInt("restaurant_max_tables"),
                    rs.getBoolean("restaurant_is_active")
            );
        }
    }

    static RowMapper<Restaurant> getRestaurantRowMapper() {
        return new RestaurantReusingRowMapper();
    }

    private static class CategoryReusingRowMapper extends LongKeyReusingRowMapper<Category> {
        private final RowMapper<Restaurant> restaurantRowMapper;

        public CategoryReusingRowMapper() {
            super("category_id");
            restaurantRowMapper = ReusingRowMappers.getRestaurantRowMapper();
        }

        @Override
        public Category mapObject(Long pk, ResultSet rs) throws SQLException {
            return new Category(
                    pk,
                    restaurantRowMapper.mapRow(rs, 1),
                    rs.getString("category_name"),
                    rs.getInt("category_order")
            );
        }
    }

    static RowMapper<Category> getCategoryRowMapper() {
        return new CategoryReusingRowMapper();
    }

    private static class ProductReusingRowMapper extends LongKeyReusingRowMapper<Product> {
        private final RowMapper<Category> categoryRowMapper;

        public ProductReusingRowMapper() {
            super("product_id");
            categoryRowMapper = ReusingRowMappers.getCategoryRowMapper();
        }

        @Override
        public Product mapObject(Long pk, ResultSet rs) throws SQLException {
            return new Product(pk,
                    categoryRowMapper.mapRow(rs, 1),
                    rs.getString("product_name"),
                    rs.getBigDecimal("product_price"),
                    rs.getString("product_description"),
                    SimpleRowMappers.readLongOrNull(rs, "product_image_id"),
                    rs.getBoolean("product_available")
            );
        }
    }

    static RowMapper<Product> getProductRowMapper() {
        return new ProductReusingRowMapper();
    }

    private static class OrderItemlessReusingRowMapper extends LongKeyReusingRowMapper<OrderItemless> {
        private final RowMapper<Restaurant> restaurantRowMapper;
        private final RowMapper<User> userRowMapper;

        public OrderItemlessReusingRowMapper() {
            super("order_id");
            restaurantRowMapper = ReusingRowMappers.getRestaurantRowMapper();
            userRowMapper = ReusingRowMappers.getUserRowMapper();
        }

        @Override
        public OrderItemless mapObject(Long pk, ResultSet rs) throws SQLException {
            return new OrderItemless(
                    rs.getLong("order_id"),
                    OrderType.fromOrdinal(rs.getInt("order_type")),
                    restaurantRowMapper.mapRow(rs, 1),
                    userRowMapper.mapRow(rs, 1),
                    SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_ordered")),
                    SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_confirmed")),
                    SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_ready")),
                    SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_delivered")),
                    SimpleRowMappers.timestampToLocalDateTimeOrNull(rs.getTimestamp("order_date_cancelled")),
                    rs.getString("order_address"),
                    rs.getInt("order_table_number"),
                    rs.getInt("order_item_count"),
                    rs.getBigDecimal("order_price")
            );
        }
    }

    static RowMapper<OrderItemless> getOrderItemlessReusingRowMapper() {
        return new OrderItemlessReusingRowMapper();
    }

    private static class OrderItemReusingRowMapper extends TripleLongKeyReusingRowMapper<OrderItem> {
        private final RowMapper<Product> productRowMapper;

        public OrderItemReusingRowMapper() {
            super("order_id", "product_id", "order_item_line_number");
            productRowMapper = ReusingRowMappers.getProductRowMapper();
        }

        @Override
        public OrderItem mapObject(Triplet<Long, Long, Long> pk, ResultSet rs) throws SQLException {
            return new OrderItem(
                    productRowMapper.mapRow(rs, 1),
                    rs.getInt("order_item_line_number"),
                    rs.getInt("order_item_quantity"),
                    rs.getString("order_item_comment")
            );
        }
    }

    static RowMapper<OrderItem> getOrderItemRowMapper() {
        return new OrderItemReusingRowMapper();
    }
}
