package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.RestaurantDetails;
import ar.edu.itba.paw.RestaurantOrderBy;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface RestaurantDao {
    long create(String name, String email, int specialty, long ownerUserId, String description, String address, int maxTables, Long logoKey, Long portrait1Kay, Long portrait2Key);

    Optional<Restaurant> getById(long restaurantId);

    PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize);

    PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialty);

    boolean delete(long restaurantId);

    List<RestaurantTags> getTags(long restaurantId);

    boolean addTag(long restaurantId, long tagId);

    boolean removeTag(long restaurantId, long tagId);

}
