package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.util.AverageCountPair;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface ReviewService {
    /**
     * Creates a review for a given orderId, or updates it if it already exists.
     * @return True if the operation was successful.
     */
    boolean createOrUpdate(long orderId, int rating, String comment);

    /**
     * Deletes the review attached to an order.
     * @return True if the operation was successful.
     */
    boolean delete(long orderId);

    /**
     * Gets an order's review, if it has one.
     */
    Optional<Review> getByOrder(long orderId);

    /**
     * Gets a restaurant's average rating from reviews, alongside the amount of reviews.
     */
    AverageCountPair getRestaurantAverage(long restaurantId);

    /**
     * Gets a restaurant's average rating from reviews after a given datetime, alongside the amount of reviews.
     */
    AverageCountPair getRestaurantAverageSince(long restaurantId, LocalDateTime datetime);

    /**
     * Gets a restaurant's reviews ordered by date descending.
     */
    List<Review> getByRestaurant(long restaurantId);

    /**
     * Gets a user's reviews ordered by date descending.
     */
    List<Review> getByUser(long userId);
}
