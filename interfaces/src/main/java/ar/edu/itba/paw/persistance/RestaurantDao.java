package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface RestaurantDao {

    Optional<Restaurant> getRestaurantById(long id);

    Restaurant createRestaurant(String name);

    boolean deleteRestaurant(long id);
}
