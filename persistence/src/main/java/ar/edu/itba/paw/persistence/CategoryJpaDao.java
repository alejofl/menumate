package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.CategoryDeletedException;
import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryJpaDao implements CategoryDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Category> getById(long categoryId) {
        return Optional.ofNullable(em.find(Category.class, categoryId));
    }

    @Override
    public Optional<Category> getByRestaurantAndOrderNum(long restaurantId, int orderNum) {
        TypedQuery<Category> query = em.createQuery(
                "FROM Category WHERE restaurantId = :restaurantId AND orderNum = :orderNum",
                Category.class
        );
        query.setParameter("restaurantId", restaurantId);
        query.setParameter("orderNum", orderNum);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public Category create(long restaurantId, String name) {
        TypedQuery<Integer> orderNumQuery = em.createQuery(
                "SELECT MAX(orderNum) FROM Category WHERE restaurantId = :restaurantId",
                Integer.class
        );
        orderNumQuery.setParameter("restaurantId", restaurantId);

        int orderNum = 1 + Utils.coalesce(orderNumQuery.getSingleResult(), 0);

        final Restaurant restaurant = em.getReference(Restaurant.class, restaurantId);
        final Category category = new Category(restaurant, name, orderNum);
        em.persist(category);
        LOGGER.info("Created category with ID {} for restaurant {}", category.getCategoryId(), restaurant.getRestaurantId());
        return category;
    }

    @Override
    public List<Category> getByRestaurantSortedByOrder(long restaurantId) {
        TypedQuery<Category> query = em.createQuery(
                "FROM Category WHERE restaurantId = :restaurantId AND deleted = false ORDER BY orderNum",
                Category.class
        );
        query.setParameter("restaurantId", restaurantId);
        return query.getResultList();
    }

    @Override
    public void delete(long categoryId) {
        final Category category = em.find(Category.class, categoryId);
        if (category == null) {
            LOGGER.error("Attempted to delete non-existing category id {}", categoryId);
            throw new CategoryNotFoundException();
        }

        if (category.getDeleted()) {
            LOGGER.error("Attempted to delete already-deleted category id {}", category.getCategoryId());
            throw new CategoryDeletedException();
        }

        category.setDeleted(true);
        em.persist(category);

        Query productQuery = em.createQuery("UPDATE Product SET deleted = true, available = false WHERE deleted = false AND categoryId = :categoryId");
        productQuery.setParameter("categoryId", categoryId);
        int productCount = productQuery.executeUpdate();

        // Close any promotions from products from this category
        // Query promoQuery = em.createQuery("UPDATE Promotion p SET p.endDate = now() WHERE (p.endDate IS NULL OR p.endDate > now()) AND p.source.categoryId = :categoryId");
        Query promoQuery = em.createNativeQuery("UPDATE promotions SET end_date = now() WHERE (end_date IS NULL OR end_date > now()) AND EXISTS(SELECT 1 FROM products WHERE products.product_id = promotions.source_id AND products.category_id = :categoryId)");
        promoQuery.setParameter("categoryId", categoryId);
        int promoRows = promoQuery.executeUpdate();

        LOGGER.info("Logical-deleted category id {} with {} products and closed {} promotions", category.getCategoryId(), productCount, promoRows);
    }

    private static final String SET_ORDER_SQL_DOWN = "SET CONSTRAINTS ALL DEFERRED;" +
            "UPDATE categories SET order_num = order_num+1 WHERE restaurant_id = ? AND order_num >= ? AND order_num < ?;" +
            "UPDATE categories SET order_num = ? WHERE category_id = ?;";

    private static final String SET_ORDER_SQL_UP = "SET CONSTRAINTS ALL DEFERRED;" +
            "UPDATE categories SET order_num = order_num-1 WHERE restaurant_id = ? AND order_num <= ? AND order_num > ?;" +
            "UPDATE categories SET order_num = ? WHERE category_id = ?;";

    @Override
    public void setOrder(Category category, int orderNum) {
        String sql = orderNum < category.getOrderNum() ? SET_ORDER_SQL_DOWN : SET_ORDER_SQL_UP;
        Query updateQuery = em.createNativeQuery(sql);
        updateQuery.setParameter(1, category.getRestaurantId());
        updateQuery.setParameter(2, orderNum);
        updateQuery.setParameter(3, category.getOrderNum());
        updateQuery.setParameter(4, orderNum);
        updateQuery.setParameter(5, category.getCategoryId());

        int rows = updateQuery.executeUpdate();

        LOGGER.info("Updated order of category id {} (restaurant {}) to {}. {} rows affected.", category.getCategoryId(), category.getRestaurantId(), orderNum, rows);
    }

    @Override
    public void moveProduct(long productId, long newCategoryId) {
        // Update the categoryId of the product with said productId, but also of all active products created from
        // promotions from this productId.
        Query query = em.createQuery(
                "UPDATE Product p SET p.categoryId = :newCategoryId WHERE deleted = false" +
                        " AND EXISTS(FROM Restaurant res JOIN Category c1 ON res.restaurantId = c1.restaurantId JOIN Category c2 ON res.restaurantId = c2.restaurantId WHERE c1.categoryId = p.categoryId AND c2.categoryId = :newCategoryId)" +
                        " AND (p.productId = :productId OR EXISTS(FROM Promotion r WHERE r.source.productId = :productId AND r.destination.productId = p.productId))"
        );
        query.setParameter("productId", productId);
        query.setParameter("newCategoryId", newCategoryId);
        int rows = query.executeUpdate();

        if (rows == 0) {
            LOGGER.error("Attempted to move product id {} to category id {} but no rows were modified", productId, newCategoryId);
            throw new IllegalStateException("exception.IllegalStateException.moveProduct");
        }

        LOGGER.info("Moved product id {} to category {}, {} product{} updated", productId, newCategoryId, rows, rows == 1 ? "" : "s");
    }
}
