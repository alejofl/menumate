package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
                .usingColumns("email", "password", "name", "image_id")
                .usingGeneratedKeyColumns("user_id");
    }

    @Override
    public User create(String email, String password, String name) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("name", name);

        final long userId = jdbcInsert.executeAndReturnKey(userData).longValue();
        return new User(userId, email, name, null, false);
    }

    @Override
    public User update(long userId, String password, String name) {
        int rows = jdbcTemplate.update(
                "UPDATE users SET password = ?, name = ? WHERE user_id = ?",
                password,
                name,
                userId
        );

        if (rows == 0)
            throw new UserNotFoundException();

        return getById(userId).orElseThrow(UserNotFoundException::new);
    }

    private static final String GET_BY_ID_SQL = "SELECT " + TableFields.USERS_FIELDS + " FROM users WHERE user_id = ?";

    @Override
    public Optional<User> getById(long userId) {
        return jdbcTemplate.query(
                GET_BY_ID_SQL,
                SimpleRowMappers.USER_ROW_MAPPER,
                userId
        ).stream().findFirst();
    }

    private static final String GET_BY_EMAIL_SQL = "SELECT " + TableFields.USERS_FIELDS + " FROM users WHERE email = ?";

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query(
                GET_BY_EMAIL_SQL,
                SimpleRowMappers.USER_ROW_MAPPER,
                email
        ).stream().findFirst();
    }

    @Override
    public boolean isUserEmailRegisteredAndConsolidated(String email) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                "SELECT EXISTS(SELECT 1 FROM users WHERE email = ? AND PASSWORD IS NOT NULL) AS ext",
                email
        );
        return rowSet.next() && rowSet.getBoolean("ext");
    }

    private static final String GET_BY_EMAIL_WITH_PASSWORD_SQL = "SELECT " + TableFields.USERS_FIELDS + ", password FROM users WHERE email = ?";

    public Optional<Pair<User, String>> getByEmailWithPassword(String email) {
        return jdbcTemplate.query(
                GET_BY_EMAIL_WITH_PASSWORD_SQL,
                SimpleRowMappers.USER_WITH_PASSWORD_ROW_MAPPER,
                email
        ).stream().findFirst();
    }
}

