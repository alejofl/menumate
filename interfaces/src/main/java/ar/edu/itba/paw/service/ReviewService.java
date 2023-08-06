package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReviewService {
    /**
     * Creates a review for a given orderId, or updates it if it already exists.
     */
    void create(long orderId, int rating, String comment);

    /**
     * Deletes the review attached to an order.
     */
    void delete(long orderId);

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
    PaginatedResult<Review> getByRestaurant(long restaurantId, int pageNumber, int pageSize);

    /**
     * Gets a user's reviews ordered by date descending.
     */
    PaginatedResult<Review> getByUser(long userId, int pageNumber, int pageSize);

    /**
     * Reply to a review made by a user.
     */
    void replyToReview(long orderId, String reply);

    /**
     * Deletes a review's reply.
     */
    void deleteReviewReply(long orderId);
}
