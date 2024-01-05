package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface TokenService {

    /**
     * This method is responsible for managing token operation for a given user.
     * If a token is present and not fresh, it refreshes the token. If token is fresh, it returns the token.
     * Otherwise, it creates a new one for the user.
     *
     * @param user The user for whom the token transaction is performed.
     * @return The token associated with the user, either existing or newly created/refreshed.
     */
    Token manageUserToken(User user);

    /**
     * Retrieves fresh tokens by token
     */
    Optional<Token> getByToken(String token);

    void delete(Token token);
}
