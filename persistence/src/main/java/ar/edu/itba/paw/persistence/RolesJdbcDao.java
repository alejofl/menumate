package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.RolesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
    public Optional<RestaurantRoleLevel> getRole(int userId, int restaurantId) {
        List<RestaurantRoleLevel> result = jdbcTemplate.query(
                "SELECT (SELECT role_level FROM restaurant_roles WHERE restaurant_id = ? AND user_id = ?) AS role_level, EXISTS(SELECT * FROM restaurants WHERE owner_user_id = ?) AS is_owner",
                SimpleRowMappers.RESTAURANT_ROLE_LEVEL_ROW_MAPPER,
                restaurantId,
                userId,
                userId
        );

        return result.isEmpty() || result.get(0) == null ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean setRole(int userId, int restaurantId, RestaurantRoleLevel level) {
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
}
