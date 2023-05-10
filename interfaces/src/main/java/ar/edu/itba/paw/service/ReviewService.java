package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.util.AverageCountPair;
import ar.edu.itba.paw.model.util.PaginatedResult;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReviewService {
    /**
     * Creates a review for a given orderId, or updates it if it already exists.
     * @return True if the operation was successful.
     */
    boolean createOrUpdate(int orderId, int rating, String comment);

    /**
     * Gets an order's review, if it has one.
     */
    Optional<Review> getByOrder(int orderId);

    /**
     * Gets a restaurant's average rating from reviews, alongside the amount of reviews.
     */
    AverageCountPair getRestaurantAverage(int restaurantId);

    /**
     * Gets a restaurant's average rating from reviews after a given datetime, alongside the amount of reviews.
     */
    AverageCountPair getRestaurantAverageSince(int restaurantId, LocalDateTime datetime);

    /**
     * Gets a restaurant's reviews ordered by date descending.
     */
    PaginatedResult<Review> getByRestaurant(int restaurantId);

    /**
     * Gets a user's reviews ordered by date descending.
     */
    PaginatedResult<Review> getByUser(int userId);
}
