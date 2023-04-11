package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Restaurant;

import java.util.Optional;

public interface RestaurantService {

    Optional<Restaurant> getRestaurantById(long id);

    Restaurant createRestaurant(String name);

    void deleteRestaurant(long id);

}
