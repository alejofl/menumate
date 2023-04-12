package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;

import java.util.Optional;

public interface RestaurantDao {
    Restaurant create(String name);

    Optional<Restaurant> getById(long restaurantId);

    boolean delete(long restaurantId);
}
