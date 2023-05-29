package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {

    Optional<Restaurant> getById(long restaurantId);

    Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2, boolean isActive, List<RestaurantTags> tags);


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

    void delete(long restaurantId);
}
