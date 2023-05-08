package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.*;
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

    public boolean checkRestaurantRole(int restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        User currentUser = ControllerUtils.getCurrentUserOrNull(userService);
        if (currentUser == null)
            return false;

        return rolesService.doesUserHaveRole(currentUser.getUserId(), restaurantId, minimumRoleLevel);
    }

    public boolean checkRestaurantOwner(int restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.OWNER);
    }

    public boolean checkRestaurantAdmin(int restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.ADMIN);
    }

    public boolean checkRestaurantManager(int restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.MANAGER);
    }

    public boolean checkRestaurantOrderHandler(int restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.ORDER_HANDLER);
    }

    public boolean checkOrderOwner(int orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        return order != null && order.getUser().getEmail().equals(ControllerUtils.getCurrentUserEmail());
    }

    public boolean checkOrderHandler(int orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        return order != null && checkRestaurantOrderHandler(order.getRestaurant().getRestaurantId());
    }

    public boolean checkOrderOwnerOrHandler(int orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        return order != null && (order.getUser().getEmail().equals(ControllerUtils.getCurrentUserEmail()) || checkRestaurantOrderHandler(order.getRestaurant().getRestaurantId()));
    }
}
