package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.UserAddressNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.persistance.UserDao;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
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
    public Optional<UserAddress> getAddressById(long userId, long addressId) {
        TypedQuery<UserAddress> query = em.createQuery(
                "FROM UserAddress WHERE userId = :userId AND addressId = :addressId",
                UserAddress.class
        );
        query.setParameter("userId", userId);
        query.setParameter("addressId", addressId);
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
    public UserAddress registerAddress(long userId, String address, String name) {
        // We need to get addresses for the user that might have the same address or name, as to handle a specific
        // side case where an UserAddress with the same userId and name but different address exists, as well as
        // another UserAddress with the same userId and address, but different name.
        // The way we handle this side case is to merge the two entities into a single one.

        // NOTE: Due to schema constraints this query will return at most 2 results.
        TypedQuery<UserAddress> query = em.createQuery(
                "FROM UserAddress WHERE userId = :userId AND (address = :address OR name = :name)",
                UserAddress.class
        );
        query.setParameter("userId", userId);
        query.setParameter("address", address);
        query.setParameter("name", name);
        List<UserAddress> addresses = query.getResultList();

        if (addresses.isEmpty()) {
            // If there are no such addresses for this user, we can register it without issues.
            final UserAddress ua = new UserAddress(userId, address, name, LocalDateTime.now());
            em.persist(ua);
            return ua;
        }

        // If there is already an UserAddress with this (userId, address) (the primary key), we'll update that one
        // instead of inserting. Either way, we need to delete the other addresses to prevent conflicts
        // delete the other UserAddress (if there is another).
        UserAddress mainAddress = addresses.stream().filter(u -> u.getAddress().equals(address)).findFirst().orElse(null);
        for (UserAddress lua : addresses)
            if (lua != mainAddress)
                em.remove(lua);
        em.flush(); // Required because Hibernate's flush order would otherwise conflict with schema constraints

        if (mainAddress == null) {
            final UserAddress ua = new UserAddress(userId, address, name, LocalDateTime.now());
            em.persist(ua);
        } else {
            mainAddress.setName(name);
            mainAddress.setLastUsed(LocalDateTime.now());
        }

        LOGGER.info("Registered named address for user id {}", userId);
        return mainAddress;
    }

    @Override
    public UserAddress updateAddress(long userId, long addressId, String address, String name) {
        final UserAddress ua = getAddressById(userId, addressId).orElseThrow(UserAddressNotFoundException::new);
        try {
            ua.setAddress(address);
            if (name != null)
                ua.setName(name);
            em.flush();
            LOGGER.info("Updated user id {} address id {}, set address{}", userId, addressId, name == null ? "" : " and name");
            return ua;
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                LOGGER.warn("Failed to update user id {} address id {} due to constraint violation", userId, addressId, e);
                throw new InvalidUserArgumentException("This address or name is already registered", e);
            }
            throw e;
        }
    }

    @Override
    public UserAddress refreshAddress(long userId, String address) {
        // Update the UserAddress if it exists
        TypedQuery<UserAddress> query = em.createQuery(
                "FROM UserAddress WHERE userId = :userId AND address = :address",
                UserAddress.class
        );
        query.setParameter("userId", userId);
        query.setParameter("address", address);
        Optional<UserAddress> maybeAddress = query.getResultList().stream().findFirst();

        // If the UserAddress exists and was updated, that's it; it's been refreshed.
        if (maybeAddress.isPresent()) {
            maybeAddress.get().setLastUsed(LocalDateTime.now());
            LOGGER.info("Refreshed address for user id {}", userId);
            return maybeAddress.get();
        }

        // Otherwise, it doesn't exist, so we need to create it
        final UserAddress ua = new UserAddress(userId, address, null, LocalDateTime.now());
        em.persist(ua);

        // And since we created an unnamed address, we need to enforce the maximum limit of unnamed addresses.
        // We make a query to delete old addresses, if necessary.
        em.flush();
        Query timestampQuery = em.createNativeQuery(GET_LASTUSED_TO_DELETE_SQL);
        List<?> resultList = timestampQuery.getResultList();

        int rows = 0;
        if (!resultList.isEmpty()) {
            LocalDateTime minLastUsed = ((Timestamp) resultList.get(0)).toLocalDateTime();
            Query deleteQuery = em.createQuery("DELETE FROM UserAddress WHERE name IS NULL AND lastUsed <= :timestamp");
            deleteQuery.setParameter("timestamp", minLastUsed);
            rows = deleteQuery.executeUpdate();
        }

        LOGGER.info("Registered unnamed address for user id {}, {} old deleted", userId, rows);
        return ua;
    }

    @Override
    public void deleteAddress(long userId, long addressId) {
        Query addressQuery = em.createQuery("DELETE FROM UserAddress WHERE userId = :userId AND addressId = :addressId");
        addressQuery.setParameter("userId", userId);
        addressQuery.setParameter("addressId", addressId);
        int rows = addressQuery.executeUpdate();

        if (rows == 0) {
            LOGGER.warn("Attempted to delete user address {} for user id {}, but no such address found", addressId, userId);
            throw new UserAddressNotFoundException();
        } else {
            LOGGER.info("Deleted an user address {} for user id {}", addressId, userId);
        }
    }
}
