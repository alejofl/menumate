package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.TokenType;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface TokenService {

    Token createOrRefresh(User user, TokenType type);

    /**
     * Retrieves fresh tokens by token
     */
    Optional<Token> getByToken(String token);

    void delete(Token token);
}
