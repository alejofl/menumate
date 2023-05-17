package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);

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

    @Transactional
    @Override
    public User createOrConsolidate(String email, String password, String name) {
        int rowsUpdated = jdbcTemplate.update(
                "UPDATE users SET password = ?, name = ? WHERE email = ? AND password IS NULL",
                password,
                name,
                email
        );

        if (rowsUpdated != 0) {
            User u = getByEmail(email).orElseThrow(UserNotFoundException::new);
            LOGGER.info("Consolidated user with ID {}", u.getUserId());
            return u;
        }


        final Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("name", name);

        final long userId = jdbcInsert.executeAndReturnKey(userData).longValue();
        LOGGER.info("Created user with ID {}", userId);
        return new User(userId, email, name, null, false);
    }

    @Transactional
    @Override
    public User createIfNotExists(String email, String name) {
        Optional<User> maybeUser = getByEmail(email);
        if (maybeUser.isPresent())
            return maybeUser.get();

        final Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", null);
        userData.put("name", name);

        final long userId = jdbcInsert.executeAndReturnKey(userData).longValue();
        LOGGER.info("Created user with ID {}", userId);
        return new User(userId, email, name, null, false);
    }

    @Override
    public void updatePassword(long userId, String password) {
        int rows = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE user_id = ?",
                password,
                userId
        );

        if (rows == 0)
            throw new UserNotFoundException();
    }

    @Override
    public void updateUserActive(long userId, boolean isActive) {
        int rows = jdbcTemplate.update(
                "UPDATE users SET is_active = ? WHERE user_id = ?",
                isActive,
                userId
        );

        if (rows == 0)
            throw new UserNotFoundException();
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

