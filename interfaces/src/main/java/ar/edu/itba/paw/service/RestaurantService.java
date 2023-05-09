package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.model.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    int create(String name, String email, int ownerUserId, String description, String address, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2);

    Optional<Restaurant> getById(int restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int getActiveCount();

    PaginatedResult<Restaurant> getSearchResults(String query, int pageNumber, int pageSize);

    List<Pair<Category, List<Product>>> getMenu(int restaurantId);

    boolean delete(int restaurantId);
}
