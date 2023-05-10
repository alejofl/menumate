package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.util.AverageCountPair;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.ReviewDao;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class ReviewJdbcDao implements ReviewDao {
    @Override
    public boolean createOrUpdate(int orderId, int rating, String comment) {
        return false;
    }

    @Override
    public Optional<Review> getByOrder(int orderId) {
        return Optional.empty();
    }

    @Override
    public AverageCountPair getRestaurantAverage(int restaurantId) {
        return null;
    }

    @Override
    public AverageCountPair getRestaurantAverageSince(int restaurantId, LocalDateTime datetime) {
        return null;
    }

    @Override
    public PaginatedResult<Review> getByRestaurant(int restaurantId) {
        return null;
    }

    @Override
    public PaginatedResult<Review> getByUser(int userId) {
        return null;
    }
}
