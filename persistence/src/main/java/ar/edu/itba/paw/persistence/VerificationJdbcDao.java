package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.util.Pair;
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
    public String generateVerificationToken(String email) {
        String token = UUID.randomUUID().toString().substring(0, 8);
        jdbcTemplate.update(
                "INSERT INTO user_verification_codes (email, code, expires) VALUES (?, ?, ?) ON CONFLICT (email) DO UPDATE SET code = excluded.code, expires = excluded.expires",
                email,
                token,
                generateTokenExpirationDate()
        );
        return token;
    }

    @Override
    public boolean verifyAndDeleteToken(String email, String token) {
        boolean success = jdbcTemplate.update(
                "DELETE FROM user_verification_codes WHERE email = ? AND code = ? AND expires>now()",
                email,
                token
        ) > 0;

        if (success) {
            return jdbcTemplate.update(
                    "UPDATE users SET is_active = true WHERE email = ?",
                    email
            ) > 0;
        }

        return false;
    }

    @Override
    public void deleteStaledVerificationTokens() {
        jdbcTemplate.update("DELETE FROM user_verification_codes WHERE expires <= now()");
    }

    private static final RowMapper<LocalDateTime> TOKEN_EXPIRES_ROW_MAPPER =
            (rs, rowNum) -> rs.getTimestamp("expires").toLocalDateTime();

    @Override
    public boolean hasActiveVerificationToken(String email) {
        Optional<LocalDateTime> tokenInfo = jdbcTemplate.query(
                "SELECT expires FROM user_verification_codes WHERE email = ?",
                TOKEN_EXPIRES_ROW_MAPPER,
                email
        ).stream().findFirst();

        return tokenInfo.isPresent() && tokenInfo.get().isAfter(LocalDateTime.now());
    }
}
