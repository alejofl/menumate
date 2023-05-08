package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RolesService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService rolesService;

    public boolean checkRestaurantRole(int restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        Integer currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        if (currentUserId == null)
            return false;

        return rolesService.doesUserHaveRole(currentUserId, restaurantId, minimumRoleLevel);
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
        PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && order.getUser().getUserId() == currentUserDetails.getUserId();
    }

    public boolean checkOrderHandler(int orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        return order != null && checkRestaurantOrderHandler(order.getRestaurant().getRestaurantId());
    }

    public boolean checkOrderOwnerOrHandler(int orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && (order.getUser().getUserId() == currentUserDetails.getUserId() || checkRestaurantOrderHandler(order.getRestaurant().getRestaurantId()));
    }
}
