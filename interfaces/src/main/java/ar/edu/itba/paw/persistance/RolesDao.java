package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.RestaurantRoleLevel;

import java.util.Optional;

public interface RolesDao {

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
}
