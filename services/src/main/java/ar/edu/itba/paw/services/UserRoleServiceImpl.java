package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserRoleDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserRoleService;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private EmailService emailService;


    @Override
    public boolean doesUserHaveRole(long userId, UserRoleLevel roleLevel) {
        if (roleLevel == null) {
            LOGGER.error("Called doesUserHaveRole() with user role null");
            throw new IllegalArgumentException("roleLevel must not be null");
        }
        User user = userDao.getById(userId).orElse(null);
        return user != null && roleLevel.equals(user.getRole());
    }

    @Override
    public Optional<UserRoleLevel> getRole(long userId) {
        User user = userDao.getById(userId).orElseThrow(UserNotFoundException::new);
        return Optional.ofNullable(user.getRole());
    }

    private void createUserAndSetRole(String email, UserRoleLevel level) {
        String name = email.split("@")[0];
        User user = userDao.create(email, null, name, LocaleContextHolder.getLocale().getLanguage());
        userRoleDao.create(user.getUserId(), level);
        // emailService.sendInvitationToRestaurantStaff(user, restaurantDao.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new));
    }

    @Override
    public void setRole(String email, UserRoleLevel roleLevel) {
        if (roleLevel == null) {
            LOGGER.error("Attempted to set null user role");
            throw new IllegalArgumentException("Cannot set null user role");
        }

        Optional<User> user = userDao.getByEmail(email);
        if (!user.isPresent()) {
            LOGGER.info("User {} does not exist. Creating user and setting role", email);
            createUserAndSetRole(email,  roleLevel);
            return;
        }

        UserRoleLevel currentRole = user.get().getRole();
        if (currentRole != null) {
            user.get().setRole(roleLevel);
            LOGGER.info("User already has a role. Changing role from {} to {}", currentRole, roleLevel);
        } else {
            userRoleDao.create(user.get().getUserId(), roleLevel);
            LOGGER.info("User {} has been set to role {}", email, roleLevel);
        }
    }

    @Override
    public void deleteRole(long userId) {
        userRoleDao.delete(userId);
    }

    @Override
    public PaginatedResult<User> getByRole(UserRoleLevel roleLevel, int pageNumber, int pageSize) {
        return userRoleDao.getByRole(roleLevel, pageNumber, pageSize);
    }
}
