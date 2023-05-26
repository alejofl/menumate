package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        LOGGER.info("Created restaurant with ID {}", restaurant.getRestaurantId());
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

    private static String generateSearchParam(String query) {
        if (query == null)
            return "%";

        String[] tokens = query.trim().toLowerCase().split(" +");
        StringBuilder searchParam = new StringBuilder("%");
        for (String token : tokens)
            searchParam.append(token.trim()).append('%');
        return searchParam.toString();
    }

    @Override
    public PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties) {
        int pageIdx = pageNumber - 1;

        String orderByColumn = getOrderByColumn(orderBy);
        String orderByDirection = descending ? "DESC" : "ASC";
        String search = generateSearchParam(query);

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT restaurant_id FROM restaurant_details AS r WHERE r.name ILIKE ?");
        appendSpecialtiesCondition(sqlBuilder, specialties);
        appendTagsCondition(sqlBuilder, tags);
        sqlBuilder.append(" ORDER BY ").append(orderByColumn).append(' ').append(orderByDirection);
        if (orderBy != null)
            sqlBuilder.append(", r.restaurant_id");
        sqlBuilder.append(" LIMIT ? OFFSET ?");

        Query nativeQuery = em.createNativeQuery(sqlBuilder.toString());
        nativeQuery.setParameter(1, search);
        nativeQuery.setParameter(2, pageSize);
        nativeQuery.setParameter(3, pageIdx * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number)n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        sqlBuilder.setLength(0);
        sqlBuilder.append("SELECT COUNT(*) AS c FROM restaurants AS r WHERE deleted = false AND is_active = true AND name ILIKE ?");
        appendSpecialtiesCondition(sqlBuilder, specialties);
        appendTagsCondition(sqlBuilder, tags);

        Query countQuery = em.createNativeQuery(sqlBuilder.toString());
        countQuery.setParameter(1, search);
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
    public void delete(Restaurant restaurant) {
        em.remove(restaurant); // TODO: Logical deletion
    }

    @Override
    public void delete(long restaurantId) {
        em.remove(em.getReference(Restaurant.class, restaurantId));
        // TODO: Logical deletion, consider leaving just one of the delete() methods and which
    }
}
