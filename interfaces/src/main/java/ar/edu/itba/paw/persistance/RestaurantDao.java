package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.util.Optional;

public interface RestaurantDao {
    long create(String name, String email, long ownerUserId, String description, String address, int maxTables, Long logoKey, Long portrait1Kay, Long portrait2Key);

    Optional<Restaurant> getById(long restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int countActive();

    PaginatedResult<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize);

    boolean delete(long restaurantId);
}
