package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.TokenNotFoundException;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.TokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class TokenJpaDao implements TokenDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(TokenJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Token> getByToken(String token) {
        final TypedQuery<Token> query = em.createQuery("FROM Token WHERE token = :token AND expiryDate > CURRENT_TIMESTAMP", Token.class);
        query.setParameter("token", token);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<Token> getByUserId(long userId) {
        final TypedQuery<Token> query = em.createQuery("FROM Token WHERE user.userId = :userId", Token.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Token create(User user, String token, LocalDateTime expiryDate) {
        final Token tkn = new Token(user, token, expiryDate);
        em.persist(tkn);
        LOGGER.info("Created token for user id {}", user.getUserId());
        return tkn;
    }

    @Override
    public Token refresh(Token token, String newToken, LocalDateTime newExpiryDate) {
        token.setExpiryDate(newExpiryDate);
        token.setToken(newToken);
        em.merge(token);
        LOGGER.info("Refreshed token for user id {}", token.getUser().getUserId());
        return token;
    }

    @Override
    public void delete(Token token) {
        try {
            final Token tkn = em.getReference(Token.class, token.getTokenId());
            em.remove(tkn);
            em.flush();
            LOGGER.info("Deleted token with ID {}", tkn.getTokenId());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Attempted to delete non-existing token id {}", token.getTokenId());
            throw new TokenNotFoundException();
        }
    }

    @Transactional
    @Override
    public void deleteStaledTokens() {
        int count = em.createQuery("DELETE FROM Token WHERE expiryDate <= now()").executeUpdate();
        LOGGER.info("Deleted {} staled user tokens", count);
    }
}
