package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Restaurant;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Restaurant create(String name);

    Optional<Restaurant> getById(long restaurantId);

    List<Pair<Category, List<Product>>> getMenu(long restaurantId);

    boolean delete(long restaurantId);

}
