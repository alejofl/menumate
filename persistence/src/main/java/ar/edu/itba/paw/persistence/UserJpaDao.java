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
        final TypedQuery<User> query = em.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<UserAddress> getAddressById(long userId, long addressId) {
        final TypedQuery<UserAddress> query = em.createQuery(
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

    private int deleteExcessUnnamedAddresses(long userId) {
        final Query timestampQuery = em.createNativeQuery(GET_LASTUSED_TO_DELETE_SQL);
        final List<?> resultList = timestampQuery.getResultList();

        if (resultList.isEmpty())
            return 0;

        final LocalDateTime minLastUsed = ((Timestamp) resultList.get(0)).toLocalDateTime();
        final Query deleteQuery = em.createQuery("DELETE FROM UserAddress WHERE name IS NULL AND lastUsed <= :timestamp");
        deleteQuery.setParameter("timestamp", minLastUsed);
        return deleteQuery.executeUpdate();
    }

    @Override
    public UserAddress registerAddress(long userId, String address, String name) {
        try {
            // Create and persist the address
            final UserAddress ua = new UserAddress(userId, address, name, LocalDateTime.now());
            em.persist(ua);
            em.flush(); // Flush so any constraint validation exceptions are raised now

            // If the address is unnamed, we must enforce the maximum limit of unnamed addresses per user.
            int rows = name == null ? deleteExcessUnnamedAddresses(userId) : 0;

            LOGGER.info("Registered {} named address for user id {} with address id {}, {} old rows deleted", name == null ? "un" : "", userId, ua.getAddressId(), rows);
            return ua;
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ConstraintViolationException) {
                final ConstraintViolationException c = (ConstraintViolationException) cause;
                final String constraintName = c.getConstraintName();
                final String problematicParam = constraintName == null ? null :
                        constraintName.contains("user_id_address") ? "address" :
                                constraintName.contains("user_id_name") ? "name" : "address or name";

                LOGGER.warn("Failed to register address for user id {} due to constraint violation on {}", userId, problematicParam == null ? "unknown field" : problematicParam, e);
                if (problematicParam != null)
                    throw new InvalidUserArgumentException("exception.InvalidUserArgumentException.registerAddress");
            }
            throw e;
        }
    }

    @Override
    public UserAddress updateAddress(long userId, long addressId, String address, String name) {
        final UserAddress ua = getAddressById(userId, addressId).orElseThrow(UserAddressNotFoundException::new);
        try {
            if (address != null)
                ua.setAddress(address);
            if (name != null)
                ua.setName(name);
            em.flush();
            LOGGER.info("Updated user id {} address id {}, set address{}", userId, addressId, name == null ? "" : " and name");
            return ua;
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ConstraintViolationException) {
                final ConstraintViolationException c = (ConstraintViolationException) cause;
                final String constraintName = c.getConstraintName();
                final String problematicParam = constraintName == null ? null :
                        constraintName.contains("user_id_address") ? "address" :
                        constraintName.contains("user_id_name") ? "name" : null;

                LOGGER.warn("Failed to update address for user id {} due to constraint violation on {}", userId, problematicParam == null ? "unknown field" : problematicParam, e);
                if (problematicParam != null)
                    throw new InvalidUserArgumentException("exception.InvalidUserArgumentException.updateAddress");
            }
            throw e;
        }
    }

    @Override
    public UserAddress refreshAddress(long userId, String address) {
        // Update the UserAddress if it exists
        final TypedQuery<UserAddress> query = em.createQuery(
                "FROM UserAddress WHERE userId = :userId AND address = :address",
                UserAddress.class
        );
        query.setParameter("userId", userId);
        query.setParameter("address", address);
        final Optional<UserAddress> maybeAddress = query.getResultList().stream().findFirst();

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

        int rows = deleteExcessUnnamedAddresses(userId);
        LOGGER.info("Registered unnamed address for user id {}, {} old deleted", userId, rows);
        return ua;
    }

    @Override
    public void deleteAddress(long userId, long addressId) {
        final Query addressQuery = em.createQuery("DELETE FROM UserAddress WHERE userId = :userId AND addressId = :addressId");
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
