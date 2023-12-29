package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.TokenType;
import ar.edu.itba.paw.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenDao {

    Optional<Token> getByToken(String token);

    Optional<Token> getByUserId(long userId, TokenType type);

    Token create(User user, TokenType type, String token, LocalDateTime expiryDate);

    void delete(Token token);

    void deleteStaledTokens();
}
