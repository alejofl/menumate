package ar.edu.itba.paw.persistance;

import java.util.Optional;

public interface BaseTokenDao {
    String generateToken(final long userId);

    Optional<Long> deleteTokenAndRetrieveUserId(String token);

    void deleteStaledTokens();

    boolean hasActiveToken(final long userId);

    boolean isValidToken(final String token);
}
