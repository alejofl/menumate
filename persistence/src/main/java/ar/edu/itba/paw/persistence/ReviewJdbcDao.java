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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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
    public boolean delete(int orderId) {
        return jdbcTemplate.update("DELETE FROM order_reviews WHERE order_id = ?", orderId) > 0;
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
        return jdbcTemplate.queryForObject(
                "SELECT AVG(CAST(order_reviews.rating AS FLOAT)) AS a, COUNT(*) AS c FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id WHERE orders.restaurant_id = ?",
                SimpleRowMappers.AVERAGE_COUNT_ROW_MAPPER,
                restaurantId
        );
    }

    @Override
    public AverageCountPair getRestaurantAverageSince(int restaurantId, LocalDateTime datetime) {
        return jdbcTemplate.queryForObject(
                "SELECT AVG(CAST(order_reviews.rating AS FLOAT)) AS a, COUNT(*) AS c FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id WHERE order_reviews.date >= ? AND orders.restaurant_id = ?",
                SimpleRowMappers.AVERAGE_COUNT_ROW_MAPPER,
                Timestamp.valueOf(datetime),
                restaurantId
        );

    }

    private static final String GET_BY_RESTAURANT_SQL = "WITH itemless_orders AS (" + OrderJdbcDao.SELECT_ITEMLESS_ORDERS + ")" +
            " SELECT " + TableFields.ORDER_REVIEW_FIELDS + ", itemless_orders.*" +
            " FROM order_reviews JOIN itemless_orders ON order_reviews.order_id = itemless_orders.order_id" +
            " WHERE itemless_orders.restaurant_id = ? ORDER BY itemless_orders.order_date_ordered, itemless_orders.order_id" +
            " LIMIT ? OFFSET ?";

    @Override
    public PaginatedResult<Review> getByRestaurant(int restaurantId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        List<Review> results = jdbcTemplate.query(
                GET_BY_RESTAURANT_SQL,
                SimpleRowMappers.ORDER_REVIEW_ROW_MAPPER,
                restaurantId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM order_reviews JOIN orders ON orders.order_id = order_reviews.order_id WHERE orders.restaurant_id = ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                restaurantId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    private static final String GET_BY_USER_SQL = "WITH itemless_orders AS (" + OrderJdbcDao.SELECT_ITEMLESS_ORDERS + ")" +
            " SELECT " + TableFields.ORDER_REVIEW_FIELDS + ", itemless_orders.*" +
            " FROM order_reviews JOIN itemless_orders ON order_reviews.order_id = itemless_orders.order_id" +
            " WHERE itemless_orders.user_id = ? ORDER BY itemless_orders.order_date_ordered, itemless_orders.order_id" +
            " LIMIT ? OFFSET ?";

    @Override
    public PaginatedResult<Review> getByUser(int userId, int pageNumber, int pageSize) {
        int pageIdx = pageNumber - 1;
        List<Review> results = jdbcTemplate.query(
                GET_BY_USER_SQL,
                SimpleRowMappers.ORDER_REVIEW_ROW_MAPPER,
                userId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                "SELECT COUNT(*) AS c FROM order_reviews JOIN orders ON orders.order_id = order_reviews.order_id WHERE orders.user_id  = ?",
                SimpleRowMappers.COUNT_ROW_MAPPER,
                userId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }
}
