package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.*;

import java.util.List;
import java.util.Optional;

public interface UserRoleDao {

    /**
     * Gets a user's role or empty if said user has no roles.
     */
    Optional<UserRole> getRole(long userId);

    /**
     * Sets a user's role.
     */
    UserRole create(long userId, UserRoleLevel roleLevel);

    /**
     * Deletes user's role .
     */
    void delete(long userId);


    /**
     * Gets the users a given role.
     */
    List<UserRole> getByRole(UserRoleLevel roleLevel);
}
