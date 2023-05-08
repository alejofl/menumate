package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.persistance.VerificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VerificationJdbcDao implements VerificationDao {

    private static final Integer TOKEN_DURATION_DAYS = 2;
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

    private Pair<Optional<String>, LocalDateTime> getTokenInfo(String email){
        return jdbcTemplate.queryForObject("SELECT code, expires FROM user_verification_codes WHERE email = ?", (rs, rowNum) -> new Pair<>(Optional.ofNullable(rs.getString("code")), rs.getTimestamp("expires").toLocalDateTime()), email);
    }
    @Override
    public String generateVerificationToken(String email){
        String token = UUID.randomUUID().toString().substring(0,8);
        jdbcTemplate.update("INSERT INTO user_verification_codes (email, code, expires) VALUES (?, ?, ?) " +
                "ON CONFLICT (email) DO UPDATE SET code=excluded.code, expires = excluded.expires",
                email, token, generateTokenExpirationDate());
        return token;
    }
    @Override
    public boolean verificationTokenIsValid(String email, String token){
        Pair<Optional<String>, LocalDateTime> tokenInfo = getTokenInfo(email);
        if(tokenInfo == null || !tokenInfo.getKey().isPresent()){
            return false;
        }
        String storedToken = tokenInfo.getKey().get();
        return storedToken.equals(token) && !tokenInfo.getValue().isBefore(LocalDateTime.now());
    }

    @Override
    public void deleteStaledVerificationTokens(){
        jdbcTemplate.update("DELETE FROM user_verification_codes WHERE expires < now()");
    }

    @Override
    public boolean deleteVerificationToken(String email) {
        return jdbcTemplate.update("DELETE FROM user_verification_codes WHERE email = ?", email) > 0;
    }

}
