package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        LOGGER.info("Created order with ID {}", order.getOrderId());
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
        // TODO: Implement. This is just a placeholder.
        List<Order> orders = em.createQuery("FROM Order WHERE user.userId = :userId", Order.class)
                .setParameter("userId", userId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return new PaginatedResult<>(orders, pageNumber, pageSize, orders.size());
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        // TODO: Implement. This is just a placeholder.
        List<Order> orders = em.createQuery("FROM Order WHERE restaurant.restaurantId = :restaurantId", Order.class)
                .setParameter("restaurantId", restaurantId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return new PaginatedResult<>(orders, pageNumber, pageSize, orders.size());
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus) {
        // TODO: Implement. This is just a placeholder.
        final String IS_PENDING_COND = "(date_confirmed IS NULL AND date_cancelled IS NULL)";
        final String IS_CONFIRMED_COND = "(date_confirmed IS NOT NULL AND date_ready IS NULL AND date_cancelled IS NULL)";
        final String IS_READY_COND = "(date_ready IS NOT NULL AND date_delivered IS NULL AND date_cancelled IS NULL)";
        final String IS_DELIVERED_COND = "date_delivered IS NOT NULL";
        final String IS_CANCELLED_COND = "date_cancelled IS NOT NULL";

        String condString = null;
        switch (orderStatus) {
            case PENDING:
                condString = IS_PENDING_COND;
            case CONFIRMED:
                condString = IS_CONFIRMED_COND;
            case READY:
                condString = IS_READY_COND;
            case DELIVERED:
                condString = IS_DELIVERED_COND;
            default:
                condString = IS_CANCELLED_COND;
        }

        List<Order> orders = em.createQuery("FROM Order WHERE restaurant.restaurantId = :restaurantId AND " + condString, Order.class)
                .setParameter("restaurantId", restaurantId)
                .setParameter("orderStatus", orderStatus)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return new PaginatedResult<>(orders, pageNumber, pageSize, orders.size());
    }
}
