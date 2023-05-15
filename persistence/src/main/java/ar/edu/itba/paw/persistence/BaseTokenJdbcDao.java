package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.BaseTokenDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class BaseTokenJdbcDao implements BaseTokenDao {

    private final String tableName;
    private static final Integer TOKEN_DURATION_DAYS = 1;
    final JdbcTemplate jdbcTemplate;
    final SimpleJdbcInsert jdbcInsert;

    static final RowMapper<Long> TOKEN_USER_ID_ROW_MAPPER =
            (rs, rowNum) -> rs.getLong("user_id");

    static final RowMapper<LocalDateTime> TOKEN_EXPIRES_ROW_MAPPER =
            (rs, rowNum) -> rs.getTimestamp("expires").toLocalDateTime();


    public BaseTokenJdbcDao(final String tableName, final DataSource ds) {
        this.tableName = tableName;
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(tableName)
                .usingColumns("user_id", "code", "expires");
    }

    private static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    TokenResult deleteTokenAndRetrieveUserId(String token) {
        Optional<Long> userId = jdbcTemplate.query(
                "SELECT user_id FROM " + tableName + " WHERE code = ?",
                TOKEN_USER_ID_ROW_MAPPER,
                token
        ).stream().findFirst();

        if (!userId.isPresent()) {
            return new TokenResult(false, null);
        }

        boolean successfullyDeleted = jdbcTemplate.update(
                "DELETE FROM " + tableName + " WHERE code = ? AND user_id = ? AND expires>now()",
                token,
                userId.get()
        ) > 0;

        return new TokenResult(successfullyDeleted, userId.get());
    }

    @Override
    public String generateToken(long userId) {
        String token = UUID.randomUUID().toString().substring(0, 32);
        jdbcTemplate.update(
                "INSERT INTO " + tableName + " (code, user_id, expires) VALUES (?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET code=excluded.code, expires = excluded.expires",
                token,
                userId,
                generateTokenExpirationDate()
        );
        return token;
    }

    @Override
    public void deleteStaledTokens() {
        jdbcTemplate.update("DELETE FROM " + tableName + " WHERE expires <= now()");
    }

    @Override
    public boolean hasActiveToken(long userId) {
        Optional<LocalDateTime> tokenInfo = jdbcTemplate.query(
                "SELECT expires FROM " + tableName + " WHERE user_id = ?",
                TOKEN_EXPIRES_ROW_MAPPER,
                userId
        ).stream().findFirst();

        return tokenInfo.isPresent() && tokenInfo.get().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isValidToken(String token) {
        return jdbcTemplate.query(
                "SELECT * FROM " + tableName + " WHERE code = ? AND expires>now()",
                TOKEN_USER_ID_ROW_MAPPER,
                token
        ).stream().findFirst().isPresent();
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
