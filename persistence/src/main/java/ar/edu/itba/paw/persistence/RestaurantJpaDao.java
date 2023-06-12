package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

@Repository
public class RestaurantJpaDao implements RestaurantDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return Optional.ofNullable(em.find(Restaurant.class, restaurantId));
    }

    @Override
    public Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, Long logoId, Long portrait1Id, Long portrait2Id, boolean isActive, List<RestaurantTags> tags) {
        final User owner = em.getReference(User.class, ownerUserId);
        final Restaurant restaurant = new Restaurant(name, email, specialty, owner, address, description, maxTables, logoId, portrait1Id, portrait2Id, isActive, tags);
        em.persist(restaurant);
        LOGGER.info("Created restaurant id {} with owner id {}", restaurant.getRestaurantId(), ownerUserId);
        return restaurant;
    }

    private static String getOrderByColumn(RestaurantOrderBy orderBy) {
        if (orderBy == null)
            return "r.restaurant_id";

        switch (orderBy) {
            case DATE:
                return "r.date_created";
            case ALPHABETIC:
                return "LOWER(r.name)";
            case RATING:
                return "r.average_rating";
            case PRICE:
                return "r.average_price";
            default:
                throw new IllegalArgumentException("Invalid or not implemented RestaurantOrderBy: " + orderBy);
        }
    }

    private static void appendSpecialtiesCondition(StringBuilder sqlBuilder, List<RestaurantSpecialty> specialties) {
        if (specialties == null || specialties.isEmpty())
            return;

        if (specialties.size() == 1) {
            sqlBuilder.append(" AND r.specialty = ");
            sqlBuilder.append(specialties.get(0).ordinal());
        } else {
            sqlBuilder.append(" AND r.specialty IN (");
            sqlBuilder.append(specialties.get(0).ordinal());
            for (int i = 1; i < specialties.size(); i++) {
                sqlBuilder.append(", ");
                sqlBuilder.append(specialties.get(i).ordinal());
            }
            sqlBuilder.append(")");
        }
    }

    private static void appendTagsCondition(StringBuilder sqlBuilder, List<RestaurantTags> tags) {
        if (tags == null || tags.isEmpty())
            return;

        if (tags.size() == 1) {
            sqlBuilder.append(" AND r.restaurant_id IN (SELECT restaurant_tags.restaurant_id FROM restaurant_tags WHERE restaurant_tags.tag_id = ");
            sqlBuilder.append(tags.get(0).ordinal());
            sqlBuilder.append(")");
        } else {
            sqlBuilder.append(" AND r.restaurant_id IN (SELECT restaurant_tags.restaurant_id FROM restaurant_tags WHERE restaurant_tags.tag_id IN (");
            sqlBuilder.append(tags.get(0).ordinal());
            for (int i = 1; i < tags.size(); i++) {
                sqlBuilder.append(", ");
                sqlBuilder.append(tags.get(i).ordinal());
            }
            sqlBuilder.append("))");
        }
    }

    private static final String NAME_SEARCH_CONDITION_SQL = " (LOWER(r.name) LIKE ? OR EXISTS(" +
            "SELECT * FROM categories LEFT OUTER JOIN products ON categories.category_id = products.category_id" +
            " WHERE categories.restaurant_id = r.restaurant_id" +
            " AND categories.deleted = false AND products.deleted = false" +
            " AND (LOWER(categories.name) LIKE ? OR LOWER(products.name) LIKE ?)" +
            "))";

    private static String generateSearchParam(String query) {
        if (query == null || query.isEmpty())
            return "%";

        query = query.replaceAll("_", "\\\\_").replaceAll("%", "\\\\%");
        String[] tokens = query.trim().toLowerCase().split(" +");
        StringBuilder searchParam = new StringBuilder("%");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty())
                searchParam.append(trimmed).append('%');
        }
        return searchParam.toString();
    }

    @Override
    public PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties) {
        Utils.validatePaginationParams(pageNumber, pageSize);
        int pageIdx = pageNumber - 1;

        String orderByColumn = getOrderByColumn(orderBy);
        String orderByDirection = descending ? "DESC" : "ASC";
        String search = generateSearchParam(query);

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT restaurant_id FROM restaurant_details AS r WHERE");
        sqlBuilder.append(NAME_SEARCH_CONDITION_SQL);
        appendSpecialtiesCondition(sqlBuilder, specialties);
        appendTagsCondition(sqlBuilder, tags);
        sqlBuilder.append(" ORDER BY ").append(orderByColumn).append(' ').append(orderByDirection);
        if (orderBy != null)
            sqlBuilder.append(", r.restaurant_id");
        sqlBuilder.append(" LIMIT ? OFFSET ?");

        Query nativeQuery = em.createNativeQuery(sqlBuilder.toString());
        nativeQuery.setParameter(1, search);
        nativeQuery.setParameter(2, search);
        nativeQuery.setParameter(3, search);
        nativeQuery.setParameter(4, pageSize);
        nativeQuery.setParameter(5, pageIdx * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number) n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, 0);

        sqlBuilder.setLength(0);
        sqlBuilder.append("SELECT COUNT(*) AS c FROM restaurants AS r WHERE deleted = false AND is_active = true AND ");
        sqlBuilder.append(NAME_SEARCH_CONDITION_SQL);
        appendSpecialtiesCondition(sqlBuilder, specialties);
        appendTagsCondition(sqlBuilder, tags);

        Query countQuery = em.createNativeQuery(sqlBuilder.toString());
        countQuery.setParameter(1, search);
        countQuery.setParameter(2, search);
        countQuery.setParameter(3, search);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        TypedQuery<RestaurantDetails> resultsQuery = em.createQuery(
                "FROM RestaurantDetails WHERE restaurantId IN :ids",
                RestaurantDetails.class
        );
        resultsQuery.setParameter("ids", idList);
        final List<RestaurantDetails> results = resultsQuery.getResultList();

        // Even though the results are sorted by the native query, when the page is brought by the HQL typed query
        // they are not sorted. Since the column to sort with might not be in RestaurantDetails but rather in
        // Restaurant, and the idList is already sorted as desired, we'll just sort them here.
        // This isn't an issue because the native query is sorted, it's just that getting the page with HQL does not
        // preserve that order.
        results.sort(Comparator.comparing(r -> idList.indexOf(r.getRestaurantId())));

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }

    @Override
    public List<Promotion> getActivePromotions(long restaurantId) {
        TypedQuery<Promotion> query = em.createQuery(
                "FROM Promotion WHERE destination.deleted = false AND destination.available = true AND destination.categoryId IN (SELECT categoryId FROM Category WHERE restaurantId = :restaurantId) ORDER BY (destination.price / source.price) DESC",
                Promotion.class
        );
        query.setParameter("restaurantId", restaurantId);
        return query.getResultList();
    }

    @Override
    public void delete(long restaurantId) {
        final Restaurant restaurant = em.find(Restaurant.class, restaurantId);
        if (restaurant == null) {
            LOGGER.error("Attempted to delete non-existing restaurant id {}", restaurantId);
            throw new RestaurantNotFoundException();
        }

        if (restaurant.getDeleted()) {
            LOGGER.error("Attempted to delete already-deleted restaurant id {}", restaurant.getRestaurantId());
            throw new IllegalStateException("Restaurant is already deleted");
        }

        restaurant.setDeleted(true);
        em.persist(restaurant);

        Query categoryQuery = em.createQuery("UPDATE Category SET deleted = true WHERE deleted = false AND restaurantId = :restaurantId");
        categoryQuery.setParameter("restaurantId", restaurantId);
        int categoryCount = categoryQuery.executeUpdate();

        Query productQuery = em.createQuery("UPDATE Product p SET p.deleted = true WHERE p.deleted = false AND EXISTS (SELECT c FROM Category c WHERE p.categoryId = c.categoryId AND c.restaurantId = :restaurantId)");
        productQuery.setParameter("restaurantId", restaurantId);
        int productCount = productQuery.executeUpdate();

        // Close any promotions from this restaurant
        Query promoQuery = em.createQuery("UPDATE Promotion p SET p.endDate = now() WHERE (p.endDate IS NULL OR p.endDate > now()) AND EXISTS(FROM Category c WHERE c.categoryId = p.source.categoryId AND c.restaurantId = :restaurantId)");
        promoQuery.setParameter("restaurantId", restaurantId);
        int promoRows = promoQuery.executeUpdate();

        LOGGER.info("Logical-deleted restaurant id {} with {} categories and {} products, closed {} promotions", restaurant.getRestaurantId(), categoryCount, productCount, promoRows);
    }
}
