package ar.edu.itba.paw.persistance;

public interface BaseTokenDao {

    String generateToken(final int userId);

    boolean verifyAndDeleteToken(final String token);

    void deleteStaledTokens();

    boolean hasActiveToken(final int userId);
}
