package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantRoleService {

    /**
     * Gets a user's role at a restaurant, or empty if said user has no roles at said restaurant.
     */
    Optional<RestaurantRoleLevel> getRole(long userId, long restaurantId);

    /**
     * Sets a user's role at a restaurant. Specify roleLevel as null to remove a user's role at a restaurant.
     */
    void setRole(String email, long restaurantId, RestaurantRoleLevel roleLevel);

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
    List<RestaurantRoleDetails> getByUser(long userId);
}
