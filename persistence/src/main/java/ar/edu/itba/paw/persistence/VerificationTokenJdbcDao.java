package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.VerificationTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository
public class VerificationTokenJdbcDao extends BaseTokenJdbcDao implements VerificationTokenDao {

    private static final String TABLE_NAME = "user_verification_codes";
    @Autowired
    public VerificationTokenJdbcDao(final DataSource ds) {
        super(TABLE_NAME, ds);
    }

    @Override
    public boolean verifyUserAndDeleteToken(String token) {
        TokenResult result = deleteTokenAndRetrieveUserId(token);

        if (result.getSuccessfullyDeleted() && result.getUserId() != null) {
            return jdbcTemplate.update(
                    "UPDATE users SET is_active = true WHERE user_id = ?",
                    result.getUserId()
            ) > 0;
        }
        return false;
    }
}
