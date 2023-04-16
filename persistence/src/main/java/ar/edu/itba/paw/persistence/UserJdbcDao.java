package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("users")
                .usingColumns("username", "password", "name", "email", "image_id")
                .usingGeneratedKeyColumns("user_id");
    }

    @Override
    public User create(String username, String password, String name, String email) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        userData.put("name", name);
        userData.put("email", email);

        final long userId = jdbcInsert.executeAndReturnKey(userData).longValue();
        return new User(userId, username, password, name, email);
    }

    @Override
    public Optional<User> getById(long userId) {
        return jdbcTemplate.query(
                "SELECT " + TableFields.USERS_FIELDS + " FROM users WHERE user_id = ?",
                RowMappers.USER_ROW_MAPPER,
                userId
        ).stream().findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query("SELECT " + TableFields.USERS_FIELDS + " FROM users WHERE email = ?", RowMappers.USER_ROW_MAPPER, email).stream().findFirst();
    }
}

