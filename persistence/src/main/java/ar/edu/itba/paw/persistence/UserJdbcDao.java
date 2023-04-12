package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email")
    );

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("users")
                .usingColumns("username", "password", "email", "image_id")
                .usingGeneratedKeyColumns("user_id");
    }

    @Override
    public Optional<User> getById(long userId) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?", userRowMapper, userId).stream().findFirst();
    }

    @Override
    public User create(String username, String password, String email) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        userData.put("email", email);

        final long userId = jdbcInsert.executeAndReturnKey(userData).longValue();
        return new User(userId, username, password, email);
    }
}
