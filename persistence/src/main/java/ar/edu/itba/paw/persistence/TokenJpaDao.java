package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.TokenType;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.TokenDao;
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
public class TokenJpaDao implements TokenDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(TokenJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Token> get(String token) {
        final TypedQuery<Token> query = em.createQuery("FROM Token WHERE token = :token", Token.class);
        query.setParameter("token", token);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Token create(User user, TokenType type, String token, LocalDateTime expiryDate) {
        final Token tkn = new Token(user, type, token, expiryDate);
        em.persist(tkn);
        LOGGER.info("Created token {} for user id {}", type.getMessageCode(), user.getUserId());
        return tkn;
    }

    @Override
    public void delete(Token token) {
        em.remove(token);
        LOGGER.info("Deleted token {} for user id {}", token.getType().getMessageCode(), token.getUser().getUserId());
    }

    @Transactional
    @Override
    public void deleteStaledTokens() {
        int count = em.createQuery("DELETE FROM Token WHERE expiryDate <= now()").executeUpdate();
        LOGGER.info("Deleted {} staled user tokens", count);
    }
}
