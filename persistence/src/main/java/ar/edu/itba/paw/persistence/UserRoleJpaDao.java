package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.persistance.UserRoleDao;
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
public class UserRoleJpaDao implements UserRoleDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserRoleJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<UserRole> getRole(long userId) {
        return Optional.ofNullable(em.find(UserRole.class, userId));
    }

    @Override
    public UserRole create(long userId, UserRoleLevel roleLevel) {
        final UserRole userRole = new UserRole(userId, roleLevel);
        em.persist(userRole);
        LOGGER.info("Created user role for user {} with level {}", userId, roleLevel);
        return userRole;
    }

    @Override
    public void delete(long userId) {
        final UserRole userRole = em.getReference(UserRole.class, userId);
        em.remove(userRole);
        LOGGER.info("Deleted user role for user {}", userId);
    }

    @Override
    public PaginatedResult<User> getByRole(UserRoleLevel roleLevel, int pageNumber, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT user_id FROM user_roles WHERE role_level = ? ORDER BY user_id DESC");
        nativeQuery.setParameter(1, roleLevel.ordinal());
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number) n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, 0);

        Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM user_roles WHERE role_level = ?");
        countQuery.setParameter(1, roleLevel.ordinal());
        int count = ((Number) countQuery.getSingleResult()).intValue();

        final TypedQuery<User> query = em.createQuery(
                "FROM User WHERE id IN :idList ORDER BY id DESC",
                User.class
        );
        query.setParameter("idList", idList);

        List<User> users = query.getResultList();
        return new PaginatedResult<>(users, pageNumber, pageSize, count);
    }
}
