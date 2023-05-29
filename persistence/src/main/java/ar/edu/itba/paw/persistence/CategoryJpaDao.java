package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.CategoryDao;
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
    public Category create(long restaurantId, String name) {
        TypedQuery<Integer> orderNumQuery = em.createQuery(
                "SELECT MAX(orderNum) FROM Category WHERE restaurantId = :restaurantId",
                Integer.class
        );
        orderNumQuery.setParameter("restaurantId", restaurantId);

        int orderNum = 1 + Optional.ofNullable(orderNumQuery.getSingleResult()).orElse(0);

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
            throw new IllegalStateException("Category is already deleted");
        }

        category.setDeleted(true);
        em.persist(category);

        Query productQuery = em.createQuery("UPDATE Product SET deleted = true WHERE deleted = false AND categoryId = :categoryId");
        productQuery.setParameter("categoryId", categoryId);
        int productCount = productQuery.executeUpdate();

        LOGGER.info("Logical-deleted category id {} with {} products", category.getCategoryId(), productCount);
    }
}
