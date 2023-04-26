package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantDao {
    Restaurant create(String name, String email);

    Optional<Restaurant> getById(int restaurantId);

    List<Restaurant> getActive(int pageNumber, int pageSize);

    List<Restaurant> getSearchResults(String[] tokens, int pageNumber, int pageSize);

    boolean delete(int restaurantId);
}
