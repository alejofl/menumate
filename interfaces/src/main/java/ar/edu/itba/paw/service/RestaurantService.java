package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.model.util.Pair;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    long create(String name, String email, long ownerUserId, String description, String address, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2);

    Optional<Restaurant> getById(long restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    int getActiveCount();

    PaginatedResult<Restaurant> getSearchResults(String query, int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByNameAsc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByNameDesc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByPriceAverageAsc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByPriceAverageDesc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByCreationDateAsc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByCreationDateDesc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByAveragePriceAsc(int pageNumber, int pageSize);

    PaginatedResult<Restaurant> getSortedByAveragePriceDesc(int pageNumber, int pageSize);

    List<Pair<Category, List<Product>>> getMenu(long restaurantId);

    boolean delete(long restaurantId);

    List<RestaurantTags> getTags(int restaurantId);

    boolean addTag(int restaurantId, int tagId);

    boolean removeTag(int restaurantId, int tagId);
}
