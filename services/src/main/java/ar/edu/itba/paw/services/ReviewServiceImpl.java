package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistance.ReviewDao;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

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

    @Override
    public void replyToReview(long orderId, String reply) {
        Review review = reviewDao.getByOrder(orderId).orElseThrow(ReviewNotFoundException::new);
        review.setReply(reply);
        LOGGER.info("Replying to review with id {}", review.getOrderId());
    }
}
