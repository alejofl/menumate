package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.model.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Restaurant create(int ownerUserId, String name, String email);

    Optional<Restaurant> getById(int restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int getActiveCount();

    PaginatedResult<Restaurant> getSearchResults(String query, int pageNumber, int pageSize);

    List<Pair<Category, List<Product>>> getMenu(int restaurantId);

    boolean delete(int restaurantId);

    double getOrderPrice(Order order);
}
