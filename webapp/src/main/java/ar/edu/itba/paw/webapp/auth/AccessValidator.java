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

    public boolean checkRestaurantRole(HttpServletRequest request, int restaurant_id, RestaurantRoleLevel minimumRoleLevel) {
        User currentUser = ControllerUtils.getCurrentUserOrNull(userService);
        if (currentUser == null)
            return false;

        Optional<RestaurantRoleLevel> roleLevel = rolesService.getRole(0, restaurant_id);
        if (!roleLevel.isPresent())
            return false;

        return roleLevel.get().ordinal() >= minimumRoleLevel.ordinal();
    }

    public boolean checkRestaurantOwner(HttpServletRequest request, int restaurant_id) {
        return checkRestaurantRole(request, restaurant_id, RestaurantRoleLevel.OWNER);
    }

    public boolean checkRestaurantAdmin(HttpServletRequest request, int restaurant_id) {
        return checkRestaurantRole(request, restaurant_id, RestaurantRoleLevel.ADMIN);
    }

    public boolean checkRestaurantManager(HttpServletRequest request, int restaurant_id) {
        return checkRestaurantRole(request, restaurant_id, RestaurantRoleLevel.MANAGER);
    }

    public boolean checkRestaurantOrderHandler(HttpServletRequest request, int restaurant_id) {
        return checkRestaurantRole(request, restaurant_id, RestaurantRoleLevel.ORDER_HANDLER);
    }

    public boolean checkOrderOwner(HttpServletRequest request, int order_id) {
        Order order = orderService.getById(order_id).orElseThrow(OrderNotFoundException::new);
        return order.getUser().getEmail().equals(ControllerUtils.getCurrentUserEmail());
    }
}
