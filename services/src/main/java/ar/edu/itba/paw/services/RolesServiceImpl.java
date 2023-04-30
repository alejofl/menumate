package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.persistance.RolesDao;
import ar.edu.itba.paw.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesDao rolesDao;

    @Override
    public Optional<RestaurantRoleLevel> getRole(int userId, int restaurantId) {
        return rolesDao.getRole(userId, restaurantId);
    }

    @Override
    public boolean setRole(int userId, int restaurantId, RestaurantRoleLevel level) {
        return rolesDao.setRole(userId, restaurantId, level);
    }
}
