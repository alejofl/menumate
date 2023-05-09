package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RolesService {

    /**
     * Gets a user's role at a restaurant, or empty if said user has no roles at said restaurant.
     */
    Optional<RestaurantRoleLevel> getRole(int userId, int restaurantId);

    /**
     * Sets a user's role at a restaurant. Specify roleLevel as null to remove a user's role at a restaurant.
     * @return True if the operation was successful
     */
    boolean setRole(int userId, int restaurantId, RestaurantRoleLevel roleLevel);

    /**
     * Returns whether a given user has a given role level or higher at a given restaurant.
     */
    boolean doesUserHaveRole(int userId, int restaurantId, RestaurantRoleLevel minimumRoleLevel);

    /**
     * Gets the users with roles for a restaurant, ordered by role in descending permissions.
     */
    List<Pair<User, RestaurantRoleLevel>> getByRestaurant(int restaurantId);
}
