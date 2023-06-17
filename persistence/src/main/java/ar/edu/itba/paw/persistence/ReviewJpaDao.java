package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistance.ReviewDao;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewJpaDao implements ReviewDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Review> getByOrder(long orderId) {
        return Optional.ofNullable(em.find(Review.class, orderId));
    }

    @Override
    public void create(long orderId, int rating, String comment) {
        final Review review = new Review(orderId, rating, comment);
        em.persist(review);
        LOGGER.info("Created review for order with ID {}", orderId);
    }

    @Override
    public void delete(long orderId) {
        Review review = em.getReference(Review.class, orderId);
        em.remove(review);
        LOGGER.info("Deleted review for order with ID {}", orderId);
    }

    @Override
    public AverageCountPair getRestaurantAverage(long restaurantId) {
        Query query = em.createQuery("SELECT COALESCE(AVG(CAST(rating AS float)), 0), COUNT(*) FROM Review WHERE order.restaurantId = :restaurantId");
        query.setParameter("restaurantId", restaurantId);
        Object[] result = (Object[]) query.getSingleResult();
        return new AverageCountPair(((Number) result[0]).floatValue(), ((Number) result[1]).intValue());
    }

    @Override
    public AverageCountPair getRestaurantAverageSince(long restaurantId, LocalDateTime datetime) {
        Query query = em.createQuery("SELECT COALESCE(AVG(CAST(rating AS float)), 0), COUNT(*) FROM Review WHERE date >= :datetime AND order.restaurantId = :restaurantId");
        query.setParameter("restaurantId", restaurantId);
        query.setParameter("datetime", datetime);
        Object[] result = (Object[]) query.getSingleResult();
        return new AverageCountPair(((Number) result[0]).floatValue(), ((Number) result[1]).intValue());
    }

    @Override
    public PaginatedResult<Review> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        Utils.validatePaginationParams(pageNumber, pageSize);

        Query nativeQuery = em.createNativeQuery("SELECT o.order_id FROM order_reviews AS r JOIN orders AS o ON r.order_id = o.order_id WHERE o.restaurant_id = ? ORDER BY r.date DESC");
        nativeQuery.setParameter(1, restaurantId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number) n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id WHERE orders.restaurant_id = ?");
        countQuery.setParameter(1, restaurantId);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, count);

        final TypedQuery<Review> query = em.createQuery("FROM Review WHERE orderId IN :idList ORDER BY date DESC", Review.class);
        query.setParameter("idList", idList);

        List<Review> reviews = query.getResultList();

        return new PaginatedResult<>(reviews, pageNumber, pageSize, count);
    }

    @Override
    public PaginatedResult<Review> getByUser(long userId, int pageNumber, int pageSize) {
        Utils.validatePaginationParams(pageNumber, pageSize);

        Query nativeQuery = em.createNativeQuery("SELECT o.order_id FROM order_reviews AS r JOIN orders AS o ON r.order_id = o.order_id WHERE o.user_id = ? ORDER BY r.date DESC");
        nativeQuery.setParameter(1, userId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number) n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id WHERE orders.user_id = ?");
        countQuery.setParameter(1, userId);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, count);

        final TypedQuery<Review> query = em.createQuery("FROM Review WHERE orderId IN :idList ORDER BY date DESC", Review.class);
        query.setParameter("idList", idList);

        List<Review> reviews = query.getResultList();

        return new PaginatedResult<>(reviews, pageNumber, pageSize, count);
    }
}
