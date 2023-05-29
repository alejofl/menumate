package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationToken;
import ar.edu.itba.paw.persistance.UserVerificationTokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserVerificationTokenJpaDao implements UserVerificationTokenDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserVerificationTokenJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<UserVerificationToken> getByUserId(long userId) {
        return Optional.ofNullable(em.find(UserVerificationToken.class, userId));
    }

    @Override
    public Optional<UserVerificationToken> getByToken(String token) {
        TypedQuery<UserVerificationToken> query = em.createQuery("FROM UserVerificationToken WHERE token = :token", UserVerificationToken.class);
        query.setParameter("token", token);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public UserVerificationToken create(User user, String token, LocalDateTime expires) {
        final UserVerificationToken vt = new UserVerificationToken(user, token, expires);
        em.persist(vt);
        LOGGER.info("Created user verification token for user id {}", user.getUserId());
        return vt;
    }

    @Override
    public void delete(UserVerificationToken userVerificationToken) {
        em.remove(userVerificationToken);
        LOGGER.info("Deleted user verification token for user id {}", userVerificationToken.getUserId());
    }

    @Transactional
    @Override
    public void deleteStaledTokens() {
        int count = em.createQuery("DELETE FROM UserVerificationToken WHERE expires <= now()").executeUpdate();
        LOGGER.info("Deleted {} staled user verification tokens", count);
    }
}
