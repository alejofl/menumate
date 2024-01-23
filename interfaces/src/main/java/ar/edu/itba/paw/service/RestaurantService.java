package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.util.PaginatedResult;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface RestaurantService {

    Optional<Restaurant> getById(long restaurantId);

    Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, Long logoId, Long portrait1Id, Long portrait2Id, boolean isActive, List<RestaurantTags> tags);


    /**
     * Searches for restaurants. Any of the nullable parameters in this function can be null to disable said filter.
     *
     * @param query       A string to search restaurants by name.
     * @param pageNumber  Specifies the maximun.
     * @param pageSize    The amount of restaurants in a page.
     * @param orderBy     Specifies how the results should be ordered.
     * @param descending  False to sort in ascending order, true to sort in descending order.
     * @param tags        Filters out restaurants that don't have one of the tags on this list.
     * @param specialties Filters out restaurants whose specialties isn't in this list.
     * @return The paginated results.
     */
    PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties);

    List<Promotion> getActivePromotions(long restaurantId);

    List<Promotion> getLivingPromotions(long restaurantId);

    Optional<RestaurantDetails> getRestaurantDetails(long restaurantId);

    Optional<Duration> getAverageOrderCompletionTime(long restaurantId, OrderType orderType);

    Restaurant update(long restaurantId, String name, RestaurantSpecialty specialty, String address, String description, List<RestaurantTags> tags);

    void updateImages(long restaurantId, Optional<Long> logoId, Optional<Long> portrait1Id, Optional<Long> portrait2Id);

    void delete(long restaurantId);

    void handleActivation(long restaurantId, boolean activate);
}
