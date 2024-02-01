package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.util.PaginatedResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RestaurantDao {

    Optional<Restaurant> getById(long restaurantId);

    Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, Long logoId, Long portrait1Id, Long portrait2Id, boolean isActive, List<RestaurantTags> tags);

    PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties);

    List<Promotion> getActivePromotions(long restaurantId);

    List<Promotion> getLivingPromotions(long restaurantId);

    Optional<Duration> getAverageOrderCompletionTime(long restaurantId, OrderType orderType, LocalDateTime since);

    Optional<RestaurantDetails> getRestaurantDetails(long restaurantId);

    void delete(long restaurantId);
}
