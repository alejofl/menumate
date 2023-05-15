package ar.edu.itba.paw.persistance;

public interface VerificationTokenDao extends BaseTokenDao {

    boolean verifyUserAndDeleteToken(final String token);
}
