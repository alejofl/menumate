package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.RestaurantRole;
import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.RestaurantRoleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
    public List<RestaurantRoleDetails> getByUser(long userId) {
        TypedQuery<RestaurantRoleDetails> query = em.createQuery(
                "FROM RestaurantRoleDetails WHERE userId = :userId ORDER BY pendingOrderCount DESC",
                RestaurantRoleDetails.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
