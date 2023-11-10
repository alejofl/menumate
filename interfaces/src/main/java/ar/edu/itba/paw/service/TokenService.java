package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.TokenType;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface TokenService {

    Token create(User user, TokenType type);

    Optional<Token> get(String token);

    void delete(Token token);

    boolean isValid(String token, TokenType type);

    void renew(Token token);
}
