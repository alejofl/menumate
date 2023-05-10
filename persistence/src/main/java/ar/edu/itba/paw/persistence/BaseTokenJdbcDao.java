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
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Long> TOKEN_USER_ID_ROW_MAPPER =
            (rs, rowNum) -> rs.getLong("user_id");

    private static final RowMapper<LocalDateTime> TOKEN_EXPIRES_ROW_MAPPER =
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

    private TokenResult deleteTokenAndRetrieveUserId(String token) {
        Optional<Long> userId = jdbcTemplate.query(
                "SELECT user_id FROM " + tableName + " WHERE code = ?",
                TOKEN_USER_ID_ROW_MAPPER,
                token
        ).stream().findFirst();

        if (!userId.isPresent()) {
            return new TokenResult(false, null);
        }

        boolean success = jdbcTemplate.update(
                "DELETE FROM " + tableName + " WHERE code = ? AND user_id = ? AND expires>now()",
                token,
                userId.get()
        ) > 0;

        return new TokenResult(success, userId.get());
    }

    @Override
    public String generateToken(int userId) {
        String token = UUID.randomUUID().toString().substring(0, 32);
        jdbcTemplate.update(
                "INSERT INTO " + tableName +" (code, user_id, expires) VALUES (?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET code=excluded.code, expires = excluded.expires",
                token,
                userId,
                generateTokenExpirationDate()
        );
        return token;
    }

    @Override
    public boolean verifyUserAndDeleteToken(String token) {
        TokenResult result = deleteTokenAndRetrieveUserId(token);

        if (result.success && result.userId != null) {
            return jdbcTemplate.update(
                    "UPDATE users SET is_active = true WHERE user_id = ?",
                    result.userId
            ) > 0;
        }
        return false;
    }

    @Override
    public void deleteStaledTokens() {
        jdbcTemplate.update("DELETE FROM " + tableName + " WHERE expires <= now()");
    }

    @Override
    public boolean hasActiveToken(int userId) {
        Optional<LocalDateTime> tokenInfo = jdbcTemplate.query(
                "SELECT expires FROM " + tableName + " WHERE user_id = ?",
                TOKEN_EXPIRES_ROW_MAPPER,
                userId
        ).stream().findFirst();

        return tokenInfo.isPresent() && tokenInfo.get().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isValidToken(String token){
        return jdbcTemplate.query(
                "SELECT * FROM " + tableName + " WHERE code = ? AND expires>now()",
                TOKEN_USER_ID_ROW_MAPPER,
                token
        ).stream().findFirst().isPresent();
    }

    @Override
    public boolean updatePasswordAndDeleteToken(String token, String newPassword) {
        TokenResult result = deleteTokenAndRetrieveUserId(token);

        if (result.success && result.userId != null) {
            return jdbcTemplate.update(
                    "UPDATE users SET password = ? WHERE user_id = ?",
                    newPassword,
                    result.userId
            ) > 0;
        }
        return false;
    }

    private static class TokenResult {
        private final boolean success;
        private final Long userId;

        public TokenResult(boolean success, Long userId) {
            this.success = success;
            this.userId = userId;
        }

        public boolean isSuccess() {
            return success;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
