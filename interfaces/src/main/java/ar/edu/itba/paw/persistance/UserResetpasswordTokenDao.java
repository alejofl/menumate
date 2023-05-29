package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserResetpasswordToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserResetpasswordTokenDao {

    Optional<UserResetpasswordToken> getByUserId(long userId);

    Optional<UserResetpasswordToken> getByToken(String token);

    UserResetpasswordToken create(User user, String token, LocalDateTime expires);

    void delete(UserResetpasswordToken UserResetpasswordToken);

    void deleteStaledTokens();
}
