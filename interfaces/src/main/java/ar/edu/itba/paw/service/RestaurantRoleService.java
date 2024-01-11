package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantRoleService {

    /**
     * Gets a user's role at a restaurant, or empty if said user has no roles at said restaurant.
     */
    Optional<RestaurantRoleLevel> getRole(long userId, long restaurantId);

    void updateRole(long userId, long restaurantId, RestaurantRoleLevel level);

    /**
     * Sets a user's role at a restaurant. Specify roleLevel as null to remove a user's role at a restaurant.
     * @return The user, and a boolean specifying whether the user was created (true) or preexisting (false).
     */
    Pair<User, Boolean> setRole(String email, long restaurantId, RestaurantRoleLevel roleLevel, String language);

    void deleteRole(long userId, long restaurantId);

    /**
     * Returns whether a given user has a given role level or higher at a given restaurant.
     */
    boolean doesUserHaveRole(long userId, long restaurantId, RestaurantRoleLevel minimumRoleLevel);

    /**
     * Gets the users with roles for a restaurant, ordered by role in descending permissions.
     */
    List<Pair<User, RestaurantRoleLevel>> getByRestaurant(long restaurantId);

    /**
     * Gets the restaurants and roles for a given user, and the amount of non-finalized orders.
     */
    PaginatedResult<RestaurantRoleDetails> getByUser(long userId, int pageNumber, int pageSize);
}
