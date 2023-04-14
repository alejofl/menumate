package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantDao {
    Restaurant create(String name, String email);

    Optional<Restaurant> getById(long restaurantId);

    List<Restaurant> getAll();

    boolean delete(long restaurantId);
}
