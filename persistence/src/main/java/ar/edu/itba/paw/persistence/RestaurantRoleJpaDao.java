package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.RestaurantRole;
import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.RestaurantRoleDao;
import ar.edu.itba.paw.util.PaginatedResult;
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
public class RestaurantRoleJpaDao implements RestaurantRoleDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantRoleJpaDao.class);

    @PersistenceContext
    private EntityManager em;


    @Override
    public Optional<RestaurantRole> getRole(long userId, long restaurantId) {
        return Optional.ofNullable(em.find(RestaurantRole.class, new RestaurantRole.RestaurantRoleId(userId, restaurantId)));
    }

    @Override
    public RestaurantRole create(long userId, long restaurantId, RestaurantRoleLevel roleLevel) {
        final RestaurantRole restaurantRole = new RestaurantRole(userId, restaurantId, roleLevel);
        em.persist(restaurantRole);
        LOGGER.info("Created restaurant role for user {} restaurant {} with level {}", userId, restaurantId, roleLevel);
        return restaurantRole;
    }

    @Override
    public void delete(long userId, long restaurantId) {
        final RestaurantRole restaurantRole = em.getReference(RestaurantRole.class, new RestaurantRole.RestaurantRoleId(userId, restaurantId));
        em.remove(restaurantRole);
        LOGGER.info("Deleted restaurant role for user {} restaurant {}", userId, restaurantId);
    }

    @Override
    public List<RestaurantRole> getByRestaurant(long restaurantId) {
        TypedQuery<RestaurantRole> query = em.createQuery(
                "FROM RestaurantRole WHERE restaurantId = :restaurantId ORDER BY level",
                RestaurantRole.class
        );
        query.setParameter("restaurantId", restaurantId);
        return query.getResultList();
    }

    @Override
    public PaginatedResult<RestaurantRoleDetails> getByUser(long userId, int pageNumber, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT restaurant_id FROM restaurant_role_details WHERE user_id = ? ORDER BY inprogress_order_count DESC, restaurant_id");
        nativeQuery.setParameter(1, userId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number) n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, 0);

        Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM restaurant_role_details WHERE user_id = ?");
        countQuery.setParameter(1, userId);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        final TypedQuery<RestaurantRoleDetails> query = em.createQuery(
                "FROM RestaurantRoleDetails WHERE userId = :userId AND restaurantId IN :idList ORDER BY pendingOrderCount DESC, restaurantId",
                RestaurantRoleDetails.class
        );
        query.setParameter("userId", userId);
        query.setParameter("idList", idList);

        List<RestaurantRoleDetails> roles = query.getResultList();
        return new PaginatedResult<>(roles, pageNumber, pageSize, count);
    }
}
