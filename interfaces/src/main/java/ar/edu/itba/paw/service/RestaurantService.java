package ar.edu.itba.paw.service;

import ar.edu.itba.paw.RestaurantDetails;
import ar.edu.itba.paw.RestaurantOrderBy;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    long create(String name, String email, int specialty, long ownerUserId, String description, String address, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2);

    Optional<Restaurant> getById(long restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    /**
     * Searches for restaurants. Any of the nullable parameters in this function can be null to disable said filter.
     * @param query A string to search restaurants by name.
     * @param pageNumber Specifies the maximun.
     * @param pageSize The amount of restaurants in a page.
     * @param orderBy Specifies how the results should be ordered.
     * @param descending False to sort in ascending order, true to sort in descending order.
     * @param tags Filters out restaurants that don't have one of the tags on this list.
     * @param specialties Filters out restaurants whose specialties isn't in this list.
     * @return The paginated results.
     */
    PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties);

    List<Pair<Category, List<Product>>> getMenu(long restaurantId);

    boolean delete(long restaurantId);

    List<RestaurantTags> getTags(long restaurantId);

    boolean addTag(long restaurantId, long tagId);

    boolean removeTag(long restaurantId, long tagId);
}
