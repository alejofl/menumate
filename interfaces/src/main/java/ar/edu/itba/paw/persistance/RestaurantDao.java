package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.util.Optional;

public interface RestaurantDao {
    int create(String name, String email, int ownerUserId, String description, String address, int maxTables, int logoKey, int portrait1Kay, int portrait2Key);

    Optional<Restaurant> getById(int restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int countActive();

    PaginatedResult<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize);

    boolean delete(int restaurantId);
}
