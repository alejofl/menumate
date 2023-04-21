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

    private static abstract class IntKeyReusingRowMapper<T> extends ReusingRowMapper<Integer, T> {
        private final String keyColumnName;

        public IntKeyReusingRowMapper(String keyColumnName) {
            this.keyColumnName = keyColumnName;
        }

        @Override
        public Integer getKey(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getInt(keyColumnName);
        }
    }

    private static abstract class TripleIntKeyReusingRowMapper<T> extends ReusingRowMapper<Triplet<Integer, Integer, Integer>, T> {
        private final String keyColumnName1;
        private final String keyColumnName2;
        private final String keyColumnName3;

        public TripleIntKeyReusingRowMapper(String keyColumnName1, String keyColumnName2, String keyColumnName3) {
            this.keyColumnName1 = keyColumnName1;
            this.keyColumnName2 = keyColumnName2;
            this.keyColumnName3 = keyColumnName3;
        }

        @Override
        public Triplet<Integer, Integer, Integer> getKey(ResultSet resultSet, int i) throws SQLException {
            return new Triplet<>(
                    resultSet.getInt(keyColumnName1),
                    resultSet.getInt(keyColumnName2),
                    resultSet.getInt(keyColumnName3)
            );
        }
    }

    private static class UserReusingRowMapper extends IntKeyReusingRowMapper<User> {
        public UserReusingRowMapper() {
            super("user_id");
        }

        @Override
        public User mapObject(Integer pk, ResultSet rs) throws SQLException {
            return new User(
                    pk,
                    rs.getString("user_email"),
                    rs.getString("user_name"),
                    rs.getInt("user_image_id"),
                    rs.getBoolean("user_is_active")
            );
        }
    }

    static RowMapper<User> getUserRowMapper() {
        return new UserReusingRowMapper();
    }

    private static class RestaurantReusingRowMapper extends IntKeyReusingRowMapper<Restaurant> {
        public RestaurantReusingRowMapper() {
            super("restaurant_id");
        }

        @Override
        public Restaurant mapObject(Integer pk, ResultSet rs) throws SQLException {
            return new Restaurant(
                    pk,
                    rs.getString("restaurant_name"),
                    rs.getString("restaurant_email"),
                    rs.getInt("restaurant_logo_id"),
                    rs.getInt("restaurant_portrait_1_id"),
                    rs.getInt("restaurant_portrait_2_id"),
                    rs.getString("restaurant_address"),
                    rs.getString("restaurant_description"),
                    rs.getBoolean("restaurant_is_active")
            );
        }
    }

    static RowMapper<Restaurant> getRestaurantRowMapper() {
        return new RestaurantReusingRowMapper();
    }

    private static class CategoryReusingRowMapper extends IntKeyReusingRowMapper<Category> {
        private final RowMapper<Restaurant> restaurantRowMapper;

        public CategoryReusingRowMapper() {
            super("category_id");
            restaurantRowMapper = ReusingRowMappers.getRestaurantRowMapper();
        }

        @Override
        public Category mapObject(Integer pk, ResultSet rs) throws SQLException {
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

    private static class ProductReusingRowMapper extends IntKeyReusingRowMapper<Product> {
        private final RowMapper<Category> categoryRowMapper;

        public ProductReusingRowMapper() {
            super("product_id");
            categoryRowMapper = ReusingRowMappers.getCategoryRowMapper();
        }

        @Override
        public Product mapObject(Integer pk, ResultSet rs) throws SQLException {
            return new Product(pk,
                    categoryRowMapper.mapRow(rs, 1),
                    rs.getString("product_name"),
                    rs.getDouble("product_price"),
                    rs.getString("product_description"),
                    rs.getInt("product_image_id"),
                    rs.getBoolean("product_available")
            );
        }
    }

    static RowMapper<Product> getProductRowMapper() {
        return new ProductReusingRowMapper();
    }

    private static class OrderItemReusingRowMapper extends TripleIntKeyReusingRowMapper<OrderItem> {
        private final RowMapper<Product> productRowMapper;

        public OrderItemReusingRowMapper() {
            super("order_id", "product_id", "order_item_line_number");
            productRowMapper = ReusingRowMappers.getProductRowMapper();
        }

        @Override
        public OrderItem mapObject(Triplet<Integer, Integer, Integer> pk, ResultSet rs) throws SQLException {
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
