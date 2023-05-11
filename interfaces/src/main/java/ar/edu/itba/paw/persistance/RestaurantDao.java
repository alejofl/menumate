package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface RestaurantDao {
    long create(String name, String email, long ownerUserId, String description, String address, int maxTables, Long logoKey, Long portrait1Kay, Long portrait2Key);

    Optional<Restaurant> getById(long restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int countActive();

    PaginatedResult<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByName(int pageNumber, int pageSize, String sort);

    PaginatedResult<Restaurant> getSortedByPriceAverage(int pageNumber, int pageSize, String sort);

    boolean delete(long restaurantId);

    List<RestaurantTags> getTags(int restaurantId);

    boolean addTag(int restaurantId, int tagId);

    boolean removeTag(int restaurantId, int tagId);

}
