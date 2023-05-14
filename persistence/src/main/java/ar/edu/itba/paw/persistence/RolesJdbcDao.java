package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.util.Triplet;
import ar.edu.itba.paw.persistance.RolesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Repository
public class RolesJdbcDao implements RolesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RolesJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<RestaurantRoleLevel> getRole(long userId, long restaurantId) {
        List<RestaurantRoleLevel> result = jdbcTemplate.query(
                "SELECT (SELECT role_level FROM restaurant_roles WHERE restaurant_id = ? AND user_id = ?) AS role_level, EXISTS(SELECT * FROM restaurants WHERE restaurant_id = ? AND owner_user_id = ?) AS is_owner",
                SimpleRowMappers.RESTAURANT_ROLE_LEVEL_ROW_MAPPER,
                restaurantId,
                userId,
                restaurantId,
                userId
        );

        return result.isEmpty() || result.get(0) == null ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean setRole(long userId, long restaurantId, RestaurantRoleLevel level) {
        if (level == RestaurantRoleLevel.OWNER)
            throw new RuntimeException("Cannot set owner role of a restaurant");

        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (?, ?, ?) ON CONFLICT (user_id, restaurant_id) DO UPDATE SET role_level=excluded.role_level",
                userId,
                restaurantId,
                level.ordinal()
        );
        return rowsAffected != 0;
    }

    @Override
    public boolean doesUserHaveRole(long userId, long restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        if (minimumRoleLevel == RestaurantRoleLevel.OWNER) {
            SqlRowSet result = jdbcTemplate.queryForRowSet(
                    "SELECT EXISTS(SELECT * FROM restaurants WHERE restaurant_id = ? AND owner_user_id = ?) AS is_owner",
                    restaurantId,
                    userId
            );

            return result.next() && result.getBoolean("is_owner");
        }

        Optional<RestaurantRoleLevel> roleLevel = getRole(userId, restaurantId);
        if (!roleLevel.isPresent())
            return false;

        return roleLevel.get().hasPermissionOf(minimumRoleLevel);
    }

    private static final RowMapper<Pair<User, RestaurantRoleLevel>> USER_ROLE_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Pair<>(
            SimpleRowMappers.USER_ROW_MAPPER.mapRow(rs, rowNum),
            RestaurantRoleLevel.fromOrdinal(rs.getInt("role_level"))
    );

    private static final String GET_BY_RESTAURANT_SQL =
            "WITH roles_grouped AS (SELECT user_id, restaurant_id, role_level FROM restaurant_roles" +
                    " UNION SELECT restaurants.owner_user_id, restaurant_id, " + RestaurantRoleLevel.OWNER.ordinal() +
                    " FROM restaurants) SELECT " + TableFields.USERS_FIELDS + ", roles_grouped.role_level" +
                    " FROM roles_grouped JOIN users ON roles_grouped.user_id = users.user_id" +
                    " WHERE roles_grouped.restaurant_id = ? ORDER BY role_level";


    @Override
    public List<Pair<User, RestaurantRoleLevel>> getByRestaurant(long restaurantId) {
        return jdbcTemplate.query(
                GET_BY_RESTAURANT_SQL,
                USER_ROLE_ROW_MAPPER,
                restaurantId
        );
    }

    private static final RowMapper<Triplet<Restaurant, RestaurantRoleLevel, Integer>> RESTAURANT_ROLE_AMOUNT_ROW_MAPPER = (ResultSet rs, int rowNum) -> new Triplet<>(
            SimpleRowMappers.RESTAURANT_ROW_MAPPER.mapRow(rs, rowNum),
            RestaurantRoleLevel.fromOrdinal(rs.getInt("role_level")),
            rs.getInt("order_count")
    );

    private static final String GET_BY_USER_SQL =
            "WITH roles_grouped AS (SELECT user_id, restaurant_id, role_level FROM restaurant_roles" +
                    " UNION SELECT restaurants.owner_user_id, restaurant_id, " + RestaurantRoleLevel.OWNER.ordinal() +
                    " FROM restaurants) SELECT " + TableFields.RESTAURANTS_FIELDS + ", roles_grouped.role_level," +
                    " (SELECT COUNT(*) FROM orders WHERE orders.restaurant_id = restaurants.restaurant_id AND " + OrderJdbcDao.IS_IN_PROGRESS_COND + ") AS order_count" +
                    " FROM roles_grouped JOIN restaurants ON roles_grouped.restaurant_id = restaurants.restaurant_id" +
                    " WHERE roles_grouped.user_id = ? AND EXISTS(SELECT * FROM restaurants WHERE restaurants.restaurant_id = roles_grouped.restaurant_id AND restaurants.deleted = false)" +
                    " ORDER BY order_count DESC";

    @Override
    public List<Triplet<Restaurant, RestaurantRoleLevel, Integer>> getByUser(long userId) {
        return jdbcTemplate.query(
                GET_BY_USER_SQL,
                RESTAURANT_ROLE_AMOUNT_ROW_MAPPER,
                userId
        );
    }

    @Override
    public boolean deleteRole(long restaurantId, long userId) {
        return jdbcTemplate.update(
                "DELETE FROM restaurant_roles WHERE restaurant_id = ? AND user_id = ?",
                restaurantId,
                userId
        ) > 0;
    }
}
