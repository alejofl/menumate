package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.persistance.UserDao;
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

        final int userId = jdbcInsert.executeAndReturnKey(userData).intValue();
        return new User(userId, email, name, 0, false);
    }

    @Override
    public User update(String email, String password, String name) {
        int userId = getByEmail(email).get().getUserId();
        jdbcTemplate.update("UPDATE users SET password = ?, name = ? WHERE user_id = ?", password, name, userId);
        return getByEmail(email).get();
    }

    @Override
    public Optional<User> getById(int userId) {
        return jdbcTemplate.query(
                "SELECT " + TableFields.USERS_FIELDS + " FROM users WHERE user_id = ?",
                SimpleRowMappers.USER_ROW_MAPPER,
                userId
        ).stream().findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query(
                "SELECT " + TableFields.USERS_FIELDS + " FROM users WHERE email = ?",
                SimpleRowMappers.USER_ROW_MAPPER,
                email
        ).stream().findFirst();
    }

    @Override
    public boolean isUserEmailRegistered(String email) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                "SELECT EXISTS(SELECT 1 FROM users WHERE email = ? AND PASSWORD IS NOT NULL) AS exists",
                email
        );
        return rowSet.next() && rowSet.getBoolean("exists");
    }

    public Optional<Pair<User, String>> getByEmailWithPassword(String email) {
        return jdbcTemplate.query(
                "SELECT " + TableFields.USERS_FIELDS + ", password FROM users WHERE email = ?",
                SimpleRowMappers.USER_WITH_PASSWORD_ROW_MAPPER,
                email
        ).stream().findFirst();
    }

    public boolean verifyAccount(String email) {
        return jdbcTemplate.update("UPDATE users SET is_active = true WHERE email = ?", email) > 0;
    }
}

