package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRoleLevel;

import java.util.List;
import java.util.Optional;

public interface UserRoleService {

    /**
     * Returns whether a given user has a given role level.
     */
    boolean doesUserHaveRole(long userId, UserRoleLevel roleLevel);

    /**
     * Gets a user's role or empty if said user has no roles.
     */
    Optional<UserRoleLevel> getRole(long userId);

    /**
     * Sets a user's role if it exists.
     */
    boolean setRole(String email, UserRoleLevel roleLevel, String language);

    /**
     * Deletes a user's role.
     */
    void deleteRole(long userId);

    /**
     * Gets the users with certain role level
     */
    List<User> getByRole(UserRoleLevel roleLevel);
}
