package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.ResetPasswordTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ResetPasswordTokenJdbcDao extends BaseTokenJdbcDao implements ResetPasswordTokenDao {

    private static final String TABLE_NAME = "user_resetpassword_codes";

    @Autowired
    public ResetPasswordTokenJdbcDao(final DataSource ds) {
        super(TABLE_NAME, ds);
    }
}
