package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.util.Optional;

public interface RestaurantDao {
    Restaurant create(String name, String email);

    Optional<Restaurant> getById(int restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int countActive();

    PaginatedResult<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize);

    boolean delete(int restaurantId);
}
