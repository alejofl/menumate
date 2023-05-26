package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserResetpasswordToken;
import ar.edu.itba.paw.persistance.UserResetpasswordTokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserResetpasswordTokenJpaDao implements UserResetpasswordTokenDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserResetpasswordTokenJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<UserResetpasswordToken> getByUserId(long userId) {
        return Optional.ofNullable(em.find(UserResetpasswordToken.class, userId));
    }

    @Override
    public Optional<UserResetpasswordToken> getByToken(String token) {
        TypedQuery<UserResetpasswordToken> query = em.createQuery("FROM UserResetpasswordToken WHERE token = :token", UserResetpasswordToken.class);
        query.setParameter("token", token);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public UserResetpasswordToken create(User user, String token, LocalDateTime expires) {
        final UserResetpasswordToken rt = new UserResetpasswordToken(user, token, expires);
        em.persist(rt);
        LOGGER.info("Created user resetpassword token for user id {}", user.getUserId());
        return rt;
    }

    @Override
    public void delete(UserResetpasswordToken userResetpasswordToken) {
        em.remove(userResetpasswordToken);
        LOGGER.info("Deleted user resetpassword token for user id {}", userResetpasswordToken.getUserId());
    }

    @Override
    public void deleteStaledTokens() {
        int count = em.createQuery("DELETE FROM UserResetpasswordToken WHERE expires >= now()").executeUpdate();
        LOGGER.info("Deleted {} staled user resetpassword tokens", count);
    }
}
