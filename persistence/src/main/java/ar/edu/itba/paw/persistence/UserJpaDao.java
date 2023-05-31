package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.persistance.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJpaDao implements UserDao {
    private static final int MAX_USER_ADDRESSES_REMEMBERED = 5;

    private final static Logger LOGGER = LoggerFactory.getLogger(UserJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        TypedQuery<User> query = em.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public User create(String email, String password, String name, String language) {
        final User user = new User(email, password, name, null, null, false, language);
        em.persist(user);
        LOGGER.info("Created {}consolidated user with ID {}", password == null ? "un" : "", user.getUserId());
        return user;
    }

    private static final String GET_LASTUSED_TO_DELETE_SQL = "SELECT last_used FROM user_addresses WHERE name IS NULL ORDER BY last_used DESC LIMIT 1 OFFSET " + MAX_USER_ADDRESSES_REMEMBERED;

    @Override
    public void registerAddress(long userId, String address, String name) {
        if (name != null) {
            // Check for sidecase where an UserAddress with the same userId and name but different address exists, as
            // well as another UserAddress with the same userId and address, but different name.
            Query addressQuery = em.createQuery("DELETE FROM UserAddress WHERE userId = :userId AND address <> :address AND name = :name");
            addressQuery.setParameter("userId", userId);
            addressQuery.setParameter("address", address);
            addressQuery.setParameter("name", name);
            int rows = addressQuery.executeUpdate();
            if (rows != 0) {
                LOGGER.info("registerAddress removed {} rows before insertion for user id {}", rows, userId);
            }
        }

        final UserAddress ua = em.merge(new UserAddress(userId, address, name, LocalDateTime.now()));
        em.flush();

        Query timestampQuery = em.createNativeQuery(GET_LASTUSED_TO_DELETE_SQL);
        List<?> resultList = timestampQuery.getResultList();

        int rows = 0;
        if (!resultList.isEmpty()) {
            LocalDateTime minLastUsed = ((Timestamp) resultList.get(0)).toLocalDateTime();
            Query query = em.createQuery("DELETE FROM UserAddress WHERE name IS NULL AND lastUsed <= :timestamp");
            query.setParameter("timestamp", minLastUsed);
            rows = query.executeUpdate();
        }

        LOGGER.info("Registered {}named address for user id {}, {} old deleted", name == null ? "un" : "", userId, rows);
    }
}
