package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.persistance.ReviewDao;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewJdbcDao implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public boolean createOrUpdate(long orderId, int rating, String comment) {
        return jdbcTemplate.update(
                "INSERT INTO order_reviews (order_id, rating, comment) VALUES (?, ?, ?) ON CONFLICT(order_id) DO UPDATE SET rating=excluded.rating, date=excluded.date, comment=excluded.comment",
                orderId,
                rating,
                comment
        ) > 0;
    }

    @Override
    public void delete(long orderId) {
        int rows = jdbcTemplate.update(
                "DELETE FROM order_reviews WHERE order_id = ?",
                orderId
        );

        if (rows == 0)
            throw new ReviewNotFoundException();
    }

    private static final String GET_BY_ORDER_SQL = "WITH itemless_orders AS (" + OrderJdbcDao.SELECT_ITEMLESS_ORDERS + ")" +
            " SELECT " + TableFields.ORDER_REVIEW_FIELDS + ", itemless_orders.*" +
            " FROM order_reviews JOIN itemless_orders ON order_reviews.order_id = itemless_orders.order_id" +
            " WHERE order_reviews.order_id = ?";

    @Override
    public Optional<Review> getByOrder(long orderId) {
        return jdbcTemplate.query(
                GET_BY_ORDER_SQL,
                SimpleRowMappers.ORDER_REVIEW_ROW_MAPPER,
                orderId

        ).stream().findFirst();
    }

    @Override
    public AverageCountPair getRestaurantAverage(long restaurantId) {
        return jdbcTemplate.queryForObject(
                "SELECT AVG(CAST(order_reviews.rating AS FLOAT)) AS a, COUNT(*) AS c FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id WHERE orders.restaurant_id = ?",
                SimpleRowMappers.AVERAGE_COUNT_ROW_MAPPER,
                restaurantId
        );
    }

    @Override
    public AverageCountPair getRestaurantAverageSince(long restaurantId, LocalDateTime datetime) {
        return jdbcTemplate.queryForObject(
                "SELECT AVG(CAST(order_reviews.rating AS FLOAT)) AS a, COUNT(*) AS c FROM order_reviews JOIN orders ON order_reviews.order_id = orders.order_id WHERE order_reviews.date >= ? AND orders.restaurant_id = ?",
                SimpleRowMappers.AVERAGE_COUNT_ROW_MAPPER,
                Timestamp.valueOf(datetime),
                restaurantId
        );
    }

    private PaginatedResult<Review> getReviewPaginatedResult(long queryTableId, int pageNumber, int pageSize, String getSql, String getCountSql) {
        int pageIdx = pageNumber - 1;
        List<Review> results = jdbcTemplate.query(
                getSql,
                SimpleRowMappers.ORDER_REVIEW_ROW_MAPPER,
                queryTableId,
                pageSize,
                pageIdx * pageSize
        );

        int count = jdbcTemplate.query(
                getCountSql,
                SimpleRowMappers.COUNT_ROW_MAPPER,
                queryTableId
        ).get(0);

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    private static final String GET_BY_RESTAURANT_SQL = "WITH itemless_orders AS (" + OrderJdbcDao.SELECT_ITEMLESS_ORDERS + ")" +
            " SELECT " + TableFields.ORDER_REVIEW_FIELDS + ", itemless_orders.*" +
            " FROM order_reviews JOIN itemless_orders ON order_reviews.order_id = itemless_orders.order_id" +
            " WHERE itemless_orders.restaurant_id = ? ORDER BY itemless_orders.order_date_ordered DESC, itemless_orders.order_id" +
            " LIMIT ? OFFSET ?";

    private static final String GET_BY_RESTAURANT_COUNT_SQL = "SELECT COUNT(*) AS c FROM order_reviews JOIN orders ON orders.order_id = order_reviews.order_id WHERE orders.restaurant_id = ?";

    @Override
    public PaginatedResult<Review> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        return getReviewPaginatedResult(restaurantId, pageNumber, pageSize, GET_BY_RESTAURANT_SQL, GET_BY_RESTAURANT_COUNT_SQL);
    }

    private static final String GET_BY_USER_SQL = "WITH itemless_orders AS (" + OrderJdbcDao.SELECT_ITEMLESS_ORDERS + ")" +
            " SELECT " + TableFields.ORDER_REVIEW_FIELDS + ", itemless_orders.*" +
            " FROM order_reviews JOIN itemless_orders ON order_reviews.order_id = itemless_orders.order_id" +
            " WHERE itemless_orders.restaurant_deleted = false AND itemless_orders.user_id = ?" +
            " ORDER BY itemless_orders.order_date_ordered DESC, itemless_orders.order_id" +
            " LIMIT ? OFFSET ?";

    private static final String GET_BY_USER_COUNT_SQL = "SELECT COUNT(*) AS c FROM order_reviews JOIN orders ON orders.order_id = order_reviews.order_id WHERE EXISTS(SELECT * FROM restaurants WHERE restaurants.restaurant_id = orders.restaurant_id AND restaurants.deleted = false) AND orders.user_id  = ?";

    @Override
    public PaginatedResult<Review> getByUser(long userId, int pageNumber, int pageSize) {
        return getReviewPaginatedResult(userId, pageNumber, pageSize, GET_BY_USER_SQL, GET_BY_USER_COUNT_SQL);
    }
}
