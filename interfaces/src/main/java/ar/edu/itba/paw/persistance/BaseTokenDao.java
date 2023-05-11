package ar.edu.itba.paw.persistance;

public interface BaseTokenDao {

    String generateToken(final int userId);

    void deleteStaledTokens();

    boolean hasActiveToken(final int userId);

    boolean isValidToken(final String token);
}
