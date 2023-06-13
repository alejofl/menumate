package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.RestaurantRole;
import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface RestaurantRoleDao {

    /**
     * Gets a user's role at a restaurant, or empty if said user has no roles at said restaurant.
     */
    Optional<RestaurantRole> getRole(long userId, long restaurantId);

    /**
     * Sets a user's role at a restaurant.
     */
    RestaurantRole create(long userId, long restaurantId, RestaurantRoleLevel roleLevel);

    void delete(long userId, long restaurantId);

    /**
     * Gets the users with roles for a restaurant, ordered by role in descending permissions.
     */
    List<RestaurantRole> getByRestaurant(long restaurantId);

    /**
     * Gets the restaurants and roles for a given user, and the amount of non-finalized orders.
     */
    PaginatedResult<RestaurantRoleDetails> getByUser(long userId, int pageNumber, int pageSize);
}
