package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.TokenGenerationException;
import ar.edu.itba.paw.persistance.BaseTokenDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class BaseTokenJdbcDao implements BaseTokenDao {

    private final String tableName;
    private static final Integer TOKEN_DURATION_DAYS = 1;
    final JdbcTemplate jdbcTemplate;
    final SimpleJdbcInsert jdbcInsert;

    static final RowMapper<Long> TOKEN_USER_ID_ROW_MAPPER =
            (rs, rowNum) -> rs.getLong("user_id");

    private final String SELECT_USER_ID_SQL;
    private final String DELETE_TOKEN_SQL;
    private final String INSERT_TOKEN_SQL;
    private final String UPDATE_TOKEN_SQL;
    private final String DELETE_STALED_SQL;
    private final String IS_VALID_TOKEN_SQL;
    private final String HAS_ACTIVE_TOKEN_SQL;

    public BaseTokenJdbcDao(final String tableName, final DataSource ds) {
        this.tableName = tableName;
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(tableName)
                .usingColumns("user_id", "code", "expires");

        SELECT_USER_ID_SQL = "SELECT user_id FROM " + tableName + " WHERE code = ?";
        DELETE_TOKEN_SQL = "DELETE FROM " + tableName + " WHERE code = ? AND user_id = ? AND expires > now()";
        INSERT_TOKEN_SQL = "INSERT INTO " + tableName + " (code, user_id, expires) VALUES (?, ?, ?)";
        UPDATE_TOKEN_SQL = "UPDATE " + tableName + " SET code = ?, expires = ? WHERE user_id = ?";
        DELETE_STALED_SQL = "DELETE FROM " + tableName + " WHERE expires <= now()";
        IS_VALID_TOKEN_SQL = "SELECT EXISTS(SELECT * FROM " + tableName + " WHERE code = ? AND expires > now()) AS ht";
        HAS_ACTIVE_TOKEN_SQL = "SELECT EXISTS(SELECT * FROM " + tableName + " WHERE user_id = ? AND expires > now()) AS ht";
    }

    private static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    protected TokenResult deleteTokenAndRetrieveUserId(String token) {
        Optional<Long> userId = jdbcTemplate.query(
                SELECT_USER_ID_SQL,
                TOKEN_USER_ID_ROW_MAPPER,
                token
        ).stream().findFirst();

        if (!userId.isPresent())
            return new TokenResult(false, null);

        boolean successfullyDeleted = jdbcTemplate.update(
                DELETE_TOKEN_SQL,
                token,
                userId.get()
        ) > 0;

        return new TokenResult(successfullyDeleted, userId.get());
    }

    @Transactional
    @Override
    public String generateToken(long userId) {
        String token = UUID.randomUUID().toString().substring(0, 32);

        int rowsUpdated = jdbcTemplate.update(
                UPDATE_TOKEN_SQL,
                token,
                Timestamp.valueOf(generateTokenExpirationDate()),
                userId
        );

        if (rowsUpdated == 0){
            int rowsInserted = jdbcTemplate.update(
                    INSERT_TOKEN_SQL,
                    token,
                    userId,
                    Timestamp.valueOf(generateTokenExpirationDate())
            );

            if (rowsInserted == 0)
                throw new TokenGenerationException();
        }
        return token;
    }

    @Transactional
    @Override
    public void deleteStaledTokens() {
        jdbcTemplate.update(DELETE_STALED_SQL);
    }

    @Transactional
    @Override
    public boolean hasActiveToken(long userId) {
        SqlRowSet result = jdbcTemplate.queryForRowSet(
                HAS_ACTIVE_TOKEN_SQL,
                userId
        );

        return result.next() && result.getBoolean("ht");
    }

    @Transactional
    @Override
    public boolean isValidToken(String token) {
        SqlRowSet result = jdbcTemplate.queryForRowSet(
                IS_VALID_TOKEN_SQL,
                token
        );

        return result.next() && result.getBoolean("ht");
    }

    static class TokenResult {
        private final boolean successfullyDeleted;
        private final Long userId;

        TokenResult(boolean successfullyDeleted, Long userId) {
            this.successfullyDeleted = successfullyDeleted;
            this.userId = userId;
        }

        public boolean getSuccessfullyDeleted() {
            return successfullyDeleted;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
