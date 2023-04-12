package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Restaurant;

import java.util.Optional;

public interface RestaurantService {
    Restaurant create(String name);

    Optional<Restaurant> getById(long restaurantId);

    boolean delete(long restaurantId);

}
