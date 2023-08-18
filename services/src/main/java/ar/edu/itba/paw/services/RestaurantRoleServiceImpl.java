package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.CannotSetRoleOfRestaurantOwnerException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.exception.RoleNotFoundException;
import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.persistance.RestaurantRoleDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.RestaurantRoleService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantRoleServiceImpl implements RestaurantRoleService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantRoleServiceImpl.class);

    @Autowired
    private RestaurantRoleDao restaurantRoleDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    @Override
    public Optional<RestaurantRoleLevel> getRole(long userId, long restaurantId) {
        final Optional<RestaurantRole> roleLevel = restaurantRoleDao.getRole(userId, restaurantId);
        if (roleLevel.isPresent())
            return Optional.of(roleLevel.get().getLevel());

        final Restaurant restaurant = restaurantDao.getById(restaurantId).orElse(null);
        if (restaurant != null && userId == restaurant.getOwnerUserId())
            return Optional.of(RestaurantRoleLevel.OWNER);

        return Optional.empty();
    }

    private User createUserAndSetRole(String email, long restaurantId, RestaurantRoleLevel level) {
        if (level == null) {
            LOGGER.error("Attempted to delete role of non existing user {}", email);
            throw new UserNotFoundException("Cannot delete role of non existing user");
        }

        final String name = email.split("@")[0];
        final User user = userDao.create(email, null, name, LocaleContextHolder.getLocale().getLanguage());
        restaurantRoleDao.create(user.getUserId(), restaurantId, level);
        emailService.sendInvitationToRestaurantStaff(user, restaurantDao.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new));
        return user;
    }

    @Transactional
    @Override
    public void updateRole(long userId, long restaurantId, RestaurantRoleLevel level) {
        if (level == RestaurantRoleLevel.OWNER) {
            LOGGER.error("Attempted to set role of user {} at restaurant {} to owner", userId, restaurantId);
            throw new IllegalArgumentException("Cannot set a restaurant role level to owner");
        }

        final Optional<RestaurantRole> maybeRole = restaurantRoleDao.getRole(userId, restaurantId);
        if (!maybeRole.isPresent()) {
            final Restaurant restaurant = restaurantDao.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
            if (userId == restaurant.getOwnerUserId())
                throw new CannotSetRoleOfRestaurantOwnerException();
            throw new RoleNotFoundException();
        }

        maybeRole.get().setLevel(level);
    }

    @Transactional
    @Override
    public User setRole(String email, long restaurantId, RestaurantRoleLevel level) {
        if (level == RestaurantRoleLevel.OWNER) {
            LOGGER.error("Attempted to set role of user {} at restaurant {} to owner", email, restaurantId);
            throw new IllegalArgumentException("Cannot set a restaurant role level to owner");
        }

        final Optional<User> user = userDao.getByEmail(email);
        if (!user.isPresent()) {
            return createUserAndSetRole(email, restaurantId, level);
        }

        if (level == null) {
            restaurantRoleDao.delete(user.get().getUserId(), restaurantId);
        } else {
            final Optional<RestaurantRole> role = restaurantRoleDao.getRole(user.get().getUserId(), restaurantId);
            if (role.isPresent()) {
                role.get().setLevel(level);
                LOGGER.info("Updated restaurant role for user {} restaurant {} to level {}", email, restaurantId, level);
            } else {
                final Restaurant restaurant = restaurantDao.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
                if (restaurant.getOwnerUserId() == user.get().getUserId())
                    throw new CannotSetRoleOfRestaurantOwnerException();
                restaurantRoleDao.create(user.get().getUserId(), restaurantId, level);
            }
        }

        return user.get();
    }

    @Transactional
    @Override
    public void deleteRole(long userId, long restaurantId) {
        try {
            restaurantRoleDao.delete(userId, restaurantId);
        } catch (RoleNotFoundException e) {
            final Restaurant restaurant = restaurantDao.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
            if (restaurant.getOwnerUserId() == userId)
                throw new CannotSetRoleOfRestaurantOwnerException();
            throw e;
        }
    }

    @Override
    public boolean doesUserHaveRole(long userId, long restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        if (minimumRoleLevel == null) {
            LOGGER.error("Called doesUserHaveRole() with role null");
            throw new IllegalArgumentException("minimumRoleLevel must not be null");
        }

        if (minimumRoleLevel == RestaurantRoleLevel.OWNER) {
            final Restaurant restaurant = restaurantDao.getById(restaurantId).orElse(null);
            return restaurant != null && restaurant.getOwnerUserId() == userId;
        }

        final RestaurantRoleLevel level = getRole(userId, restaurantId).orElse(null);
        return level != null && level.hasPermissionOf(minimumRoleLevel);
    }

    @Override
    public List<Pair<User, RestaurantRoleLevel>> getByRestaurant(long restaurantId) {
        List<Pair<User, RestaurantRoleLevel>> roles = new ArrayList<>();

        final Restaurant restaurant = restaurantDao.getById(restaurantId).orElse(null);
        if (restaurant == null)
            return Collections.emptyList();
        roles.add(new Pair<>(restaurant.getOwner(), RestaurantRoleLevel.OWNER));

        final List<RestaurantRole> otherRoles = restaurantRoleDao.getByRestaurant(restaurantId);
        for (RestaurantRole r : otherRoles)
            roles.add(new Pair<>(r.getUser(), r.getLevel()));

        return roles;
    }

    @Override
    public PaginatedResult<RestaurantRoleDetails> getByUser(long userId, int pageNumber, int pageSize) {
        return restaurantRoleDao.getByUser(userId, pageNumber, pageSize);
    }
}
