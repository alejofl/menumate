package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderJpaDao implements OrderDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    private Order create(OrderType orderType, long restaurantId, long userId, String address, Integer tableNumber) {
        final Restaurant restaurant = em.getReference(Restaurant.class, restaurantId);
        final User user = em.getReference(User.class, userId);
        final Order order = new Order(orderType, restaurant, user, address, tableNumber);
        em.persist(order);
        LOGGER.info("Created order id {} type {} for restaurant {} by user {}", order.getOrderId(), orderType, restaurantId, userId);
        return order;
    }

    @Override
    public Order createDelivery(long restaurantId, long userId, String address) {
        return this.create(OrderType.DELIVERY, restaurantId, userId, address, null);
    }

    @Override
    public Order createTakeaway(long restaurantId, long userId) {
        return this.create(OrderType.TAKEAWAY, restaurantId, userId, null, null);
    }

    @Override
    public Order createDineIn(long restaurantId, long userId, int tableNumber) {
        return this.create(OrderType.DINE_IN, restaurantId, userId, null, tableNumber);
    }

    @Override
    public OrderItem createOrderItem(long productId, int lineNumber, int quantity, String comment) {
        final Product product = em.getReference(Product.class, productId);
        return new OrderItem(product, lineNumber, quantity, comment);
    }

    @Override
    public Optional<Order> getById(long orderId) {
        return Optional.ofNullable(em.find(Order.class, orderId));
    }

    @Override
    public PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize) {
        Utils.validatePaginationParams(pageNumber, pageSize);

        Query nativeQuery = em.createNativeQuery("SELECT order_id FROM orders WHERE user_id = ? ORDER BY date_ordered DESC");
        nativeQuery.setParameter(1, userId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number)n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, 0);

        Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM orders WHERE user_id = ?");
        countQuery.setParameter(1, userId);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        final TypedQuery<Order> query = em.createQuery("FROM Order WHERE orderId IN :idList ORDER BY dateOrdered DESC", Order.class);
        query.setParameter("idList", idList);

        List<Order> orders = query.getResultList();

        return new PaginatedResult<>(orders, pageNumber, pageSize, count);
    }

    private static String getCoditionString(OrderStatus orderStatus) {
        if (orderStatus == null)
            return "";

        switch (orderStatus) {
            case PENDING:
                return " AND (date_confirmed IS NULL AND date_cancelled IS NULL)";
            case CONFIRMED:
                return " AND (date_confirmed IS NOT NULL AND date_ready IS NULL AND date_cancelled IS NULL)";
            case READY:
                return " AND (date_ready IS NOT NULL AND date_delivered IS NULL AND date_cancelled IS NULL)";
            case DELIVERED:
                return " AND date_delivered IS NOT NULL";
            default:
                return " AND date_cancelled IS NOT NULL";
        }
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        Utils.validatePaginationParams(pageNumber, pageSize);

        String condString = getCoditionString(orderStatus);

        Query nativeQuery = em.createNativeQuery("SELECT order_id FROM orders WHERE restaurant_id = ?" + condString + " ORDER BY date_ordered DESC");
        nativeQuery.setParameter(1, restaurantId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number)n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, 0);

        Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM orders WHERE restaurant_id = ?" + condString);
        countQuery.setParameter(1, restaurantId);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        final TypedQuery<Order> query = em.createQuery("FROM Order WHERE orderId IN :idList ORDER BY dateOrdered DESC", Order.class);
        query.setParameter("idList", idList);

        List<Order> orders = query.getResultList();

        return new PaginatedResult<>(orders, pageNumber, pageSize, count);
    }
}
