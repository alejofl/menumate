package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.ResetPasswordTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Repository
public class ResetPasswordTokenJdbcDao extends BaseTokenJdbcDao implements ResetPasswordTokenDao {

    private static final String TABLE_NAME = "user_resetpassword_codes";

    @Autowired
    public ResetPasswordTokenJdbcDao(final DataSource ds) {
        super(TABLE_NAME, ds);
    }

    @Transactional
    @Override
    public boolean updatePasswordAndDeleteToken(String token, String newPassword) {
        TokenResult result = deleteTokenAndRetrieveUserId(token);

        if (result.getSuccessfullyDeleted() && result.getUserId() != null) {
            return jdbcTemplate.update(
                    "UPDATE users SET password = ? WHERE user_id = ?",
                    newPassword,
                    result.getUserId()
            ) > 0;
        }
        return false;
    }
}
