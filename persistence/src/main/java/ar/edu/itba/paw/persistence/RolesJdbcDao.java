package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.RolesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
        return jdbcTemplate.query(
                "SELECT role_level FROM restaurant_roles WHERE user_id = ? AND restaurant_id = ?",
                SimpleRowMappers.RESTAURANT_ROLE_LEVEL_ROW_MAPPER,
                userId,
                restaurantId
        ).stream().findFirst();
    }

    @Override
    public boolean setRole(int userId, int restaurantId, RestaurantRoleLevel level) {
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO restaurant_roles (user_id, restaurant_id, role_level) VALUES (?, ?, ?) ON CONFLICT (user_id, restaurant_id) DO UPDATE SET role_level=excluded.role_level",
                userId,
                restaurantId,
                level.ordinal()

        );
        return rowsAffected != 0;
    }
}
