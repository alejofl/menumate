package ar.edu.itba.paw.persistance;

public interface BaseTokenDao {

    String generateToken(final int userId);

    boolean verifyUserAndDeleteToken(final String token);

    boolean updatePasswordAndDeleteToken(String token, String newPassword);

    void deleteStaledTokens();

    boolean hasActiveToken(final int userId);

    boolean isValidToken(final String token);
}
