package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RolesService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import ar.edu.itba.paw.webapp.exception.OrderNotFoundException;
import ar.edu.itba.paw.webapp.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class AccessValidator {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService rolesService;

    public boolean checkRestaurantRole(HttpServletRequest request, int restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        User currentUser = ControllerUtils.getCurrentUserOrNull(userService);
        if (currentUser == null)
            return false;

        return rolesService.doesUserHaveRole(currentUser.getUserId(), restaurantId, minimumRoleLevel);
    }

    public boolean checkRestaurantOwner(HttpServletRequest request, int restaurantId) {
        return checkRestaurantRole(request, restaurantId, RestaurantRoleLevel.OWNER);
    }

    public boolean checkRestaurantAdmin(HttpServletRequest request, int restaurantId) {
        return checkRestaurantRole(request, restaurantId, RestaurantRoleLevel.ADMIN);
    }

    public boolean checkRestaurantManager(HttpServletRequest request, int restaurantId) {
        return checkRestaurantRole(request, restaurantId, RestaurantRoleLevel.MANAGER);
    }

    public boolean checkRestaurantOrderHandler(HttpServletRequest request, int restaurantId) {
        return checkRestaurantRole(request, restaurantId, RestaurantRoleLevel.ORDER_HANDLER);
    }

    public boolean checkOrderOwner(HttpServletRequest request, int orderId) {
        Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        return order.getUser().getEmail().equals(ControllerUtils.getCurrentUserEmail());
    }
}
