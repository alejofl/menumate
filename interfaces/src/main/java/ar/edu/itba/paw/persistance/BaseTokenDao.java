package ar.edu.itba.paw.persistance;

public interface BaseTokenDao {
    String generateToken(final long userId);

    void deleteStaledTokens();

    boolean hasActiveToken(final long userId);

    boolean isValidToken(final String token);
}
