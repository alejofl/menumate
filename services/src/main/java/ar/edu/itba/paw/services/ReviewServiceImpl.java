package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistance.ReviewDao;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Override
    @Transactional
    public void create(long orderId, int rating, String comment) {
        reviewDao.create(orderId, rating, comment);
    }

    @Override
    @Transactional
    public void delete(long orderId) {
        reviewDao.delete(orderId);
    }

    @Override
    public Optional<Review> getByOrder(long orderId) {
        return reviewDao.getByOrder(orderId);
    }

    @Override
    public AverageCountPair getRestaurantAverage(long restaurantId) {
        return reviewDao.getRestaurantAverage(restaurantId);
    }

    @Override
    public AverageCountPair getRestaurantAverageSince(long restaurantId, LocalDateTime datetime) {
        return reviewDao.getRestaurantAverageSince(restaurantId, datetime);
    }

    @Override
    public PaginatedResult<Review> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        return reviewDao.getByRestaurant(restaurantId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Review> getByUser(long userId, int pageNumber, int pageSize) {
        return reviewDao.getByUser(userId, pageNumber, pageSize);
    }
}
