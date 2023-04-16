package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.*;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Restaurant create(String name, String email);

    Optional<Restaurant> getById(long restaurantId);

    List<Restaurant> getAll();

    List<Pair<Category, List<Product>>> getMenu(long restaurantId);

    boolean delete(long restaurantId);

    double getOrderPrice(Order order);
}
