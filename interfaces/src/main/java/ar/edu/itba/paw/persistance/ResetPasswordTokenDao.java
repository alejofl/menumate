package ar.edu.itba.paw.persistance;

public interface ResetPasswordTokenDao extends BaseTokenDao {

    boolean updatePasswordAndDeleteToken(String token, String newPassword);

}
