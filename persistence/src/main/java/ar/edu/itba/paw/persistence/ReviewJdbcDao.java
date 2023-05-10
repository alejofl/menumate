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

    private static final String GET_BY_ORDER_SQL = "WITH itemless_orders AS (" + OrderJdbcDao.SELECT_ITEMLESS_ORDERS + ")" +
            " SELECT " + TableFields.ORDER_REVIEW_FIELDS + ", itemless_orders.*" +
            " FROM order_reviews JOIN itemless_orders ON order_reviews.order_id = itemless_orders.order_id" +
            " WHERE order_reviews.order_id = ?";

    @Override
    public Optional<Review> getByOrder(int orderId) {
        return jdbcTemplate.query(
                GET_BY_ORDER_SQL,
                SimpleRowMappers.ORDER_REVIEW_ROW_MAPPER,
                orderId

        ).stream().findFirst();
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
