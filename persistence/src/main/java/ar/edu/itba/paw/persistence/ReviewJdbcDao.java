package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.util.AverageCountPair;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class ReviewJdbcDao implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ReviewJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("order_reviews")
                .usingColumns("order_id", "rating", "comment");
    }

    @Override
    public boolean createOrUpdate(int orderId, int rating, String comment) {
        return jdbcTemplate.update(
                "INSERT INTO order_reviews (order_id, rating, comment) VALUES (?, ?, ?) ON CONFLICT(order_id) DO UPDATE SET rating=excluded.rating, date=excluded.date, comment=excluded.comment",
                orderId,
                rating,
                comment
        ) > 0;
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
