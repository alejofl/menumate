package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.RolesDao;
import ar.edu.itba.paw.service.RolesService;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.util.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesDao rolesDao;

    @Override
    public Optional<RestaurantRoleLevel> getRole(long userId, long restaurantId) {
        return rolesDao.getRole(userId, restaurantId);
    }

    @Override
    public void setRole(long userId, long restaurantId, RestaurantRoleLevel level) {
        if (level == null)
            rolesDao.deleteRole(restaurantId, userId);
        else
            rolesDao.setRole(userId, restaurantId, level);
    }

    @Override
    public void deleteRole(long restaurantId, long userId) {
        rolesDao.deleteRole(restaurantId, userId);
    }

    @Override
    public boolean doesUserHaveRole(long userId, long restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        return rolesDao.doesUserHaveRole(userId, restaurantId, minimumRoleLevel);
    }

    @Override
    public List<Pair<User, RestaurantRoleLevel>> getByRestaurant(long restaurantId) {
        return rolesDao.getByRestaurant(restaurantId);
    }

    @Override
    public List<Triplet<Restaurant, RestaurantRoleLevel, Integer>> getByUser(long userId) {
        return rolesDao.getByUser(userId);
    }
}
