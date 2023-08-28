package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.UserRoleNotFoundException;
import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.persistance.UserRoleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
        try {
            final UserRole userRole = em.getReference(UserRole.class, userId);
            em.remove(userRole);
            em.flush();
            LOGGER.info("Deleted user role for user {}", userId);
        } catch (EntityNotFoundException e) {
            LOGGER.warn("Failed to delete user role for user id {}", userId, e);
            throw new UserRoleNotFoundException();
        }
    }

    @Override
    public List<UserRole> getByRole(UserRoleLevel roleLevel) {
        final TypedQuery<UserRole> query = em.createQuery(
                "FROM UserRole ORDER BY userId DESC",
                UserRole.class
        );

        return query.getResultList();
    }
}
