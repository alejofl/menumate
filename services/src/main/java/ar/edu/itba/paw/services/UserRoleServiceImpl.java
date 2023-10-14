package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.UserRoleNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.UserRoleDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Optional<UserRole> userRole = userRoleDao.getRole(userId);
        return userRole.isPresent() && roleLevel.equals(userRole.get().getLevel());
    }

    @Override
    public Optional<UserRoleLevel> getRole(long userId) {
        Optional<UserRole> userRole = userRoleDao.getRole(userId);
        return userRole.map(UserRole::getLevel);
    }

    private void createUserAndSetRole(String email, UserRoleLevel level) {
        String name = email.split("@")[0];
        User user = userDao.create(email, null, name, LocaleContextHolder.getLocale().getLanguage());
        userRoleDao.create(user.getUserId(), level);
        emailService.sendInvitationToUser(user, level.getMessageCode().replaceAll("^ROLE_", "").toLowerCase());
    }

    @Transactional
    @Override
    public boolean setRole(String email, UserRoleLevel roleLevel) {
        if (roleLevel == null) {
            LOGGER.error("Attempted to set not-existing user role");
            throw new UserRoleNotFoundException("Cannot set invalid user role");
        }

        Optional<User> user = userDao.getByEmail(email);
        if (!user.isPresent()) {
            LOGGER.info("User {} does not exist. Creating user and setting role {}", email, roleLevel);
            createUserAndSetRole(email, roleLevel);
            return true;
        }
        Optional<UserRole> currentRole = userRoleDao.getRole(user.get().getUserId());
        if (currentRole.isPresent()) {
            currentRole.get().setLevel(roleLevel);
            LOGGER.info("User already has a role. Changing role from {} to {}", currentRole.get(), roleLevel);
        } else {
            userRoleDao.create(user.get().getUserId(), roleLevel);
            LOGGER.info("User {} has been set to role {}", email, roleLevel);
        }
        return true;
    }

    @Transactional
    @Override
    public void deleteRole(long userId) {
        userRoleDao.delete(userId);
    }

    @Override
    public List<User> getByRole(UserRoleLevel roleLevel) {
        return userRoleDao.getByRole(roleLevel);
    }
}
