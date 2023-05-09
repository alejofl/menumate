package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.VerificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VerificationJdbcDao implements VerificationDao {

    private static final Integer TOKEN_DURATION_DAYS = 1;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Long> TOKEN_USER_ID_ROW_MAPPER =
            (rs, rowNum) -> rs.getLong("user_id");

    private static final RowMapper<LocalDateTime> TOKEN_EXPIRES_ROW_MAPPER =
            (rs, rowNum) -> rs.getTimestamp("expires").toLocalDateTime();

    @Autowired
    public VerificationJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("user_verification_codes")
                .usingColumns("email", "code", "expires");
    }

    private static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    @Override
    public String generateVerificationToken(final int userId) {
        String token = UUID.randomUUID().toString().substring(0, 32);
        jdbcTemplate.update(
                "INSERT INTO user_verification_codes (code, user_id, expires) VALUES (?, ?, ?) ON CONFLICT (code, user_id) DO UPDATE SET code = excluded.code, expires = excluded.expires",
                token,
                userId,
                generateTokenExpirationDate()
        );
        return token;
    }

    @Override
    public boolean verifyAndDeleteToken(final String token) {

        Optional<Long> userId = jdbcTemplate.query(
                "SELECT user_id FROM user_verification_codes WHERE code = ?",
                TOKEN_USER_ID_ROW_MAPPER,
                token
        ).stream().findFirst();

        if (!userId.isPresent()) {
            return false;
        }

        boolean success = jdbcTemplate.update(
                "DELETE FROM user_verification_codes WHERE code = ? AND user_id = ? AND expires>now()",
                token,
                userId.get()
        ) > 0;

        if (success) {
            return jdbcTemplate.update(
                    "UPDATE users SET is_active = true WHERE user_id = ?",
                    userId.get()
            ) > 0;
        }
        return false;
    }

    @Override
    public void deleteStaledVerificationTokens() {
        jdbcTemplate.update("DELETE FROM user_verification_codes WHERE expires <= now()");
    }

    @Override
    public boolean hasActiveVerificationToken(final int userId) {
        Optional<LocalDateTime> tokenInfo = jdbcTemplate.query(
                "SELECT expires FROM user_verification_codes WHERE user_id = ?",
                TOKEN_EXPIRES_ROW_MAPPER,
                userId
        ).stream().findFirst();

        return tokenInfo.isPresent() && tokenInfo.get().isAfter(LocalDateTime.now());
    }
}
