package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserVerificationTokenDao {

    Optional<UserVerificationToken> getByUserId(long userId);

    Optional<UserVerificationToken> getByToken(String token);

    UserVerificationToken create(User user, String token, LocalDateTime expires);

    void delete(UserVerificationToken userVerificationToken);

    void deleteStaledTokens();
}
