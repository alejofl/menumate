package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistance.VerificationTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository
public class VerificationTokenJdbcDao extends BaseTokenJdbcDao implements VerificationTokenDao {

    private static final String TABLE_NAME = "user_resetpassword_codes";
    @Autowired
    public VerificationTokenJdbcDao(final DataSource ds) {
        super(TABLE_NAME, ds);
    }

}
