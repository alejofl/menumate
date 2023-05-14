package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.util.Triplet;

import java.util.List;
import java.util.Optional;

public interface RolesService {

    /**
     * Gets a user's role at a restaurant, or empty if said user has no roles at said restaurant.
     */
    Optional<RestaurantRoleLevel> getRole(long userId, long restaurantId);

    /**
     * Sets a user's role at a restaurant. Specify roleLevel as null to remove a user's role at a restaurant.
     * @return True if the operation was successful
     */
    boolean setRole(long userId, long restaurantId, RestaurantRoleLevel roleLevel);

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
    List<Triplet<Restaurant, RestaurantRoleLevel, Integer>> getByUser(long userId);

    boolean deleteRole(long restaurantId, long userId);
}
