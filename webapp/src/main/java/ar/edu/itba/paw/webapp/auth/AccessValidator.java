package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.OrderItemless;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RolesService;
import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RolesService rolesService;

    public boolean checkRestaurantRole(long restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        if (currentUserId == null)
            return false;

        return rolesService.doesUserHaveRole(currentUserId, restaurantId, minimumRoleLevel);
    }

    public boolean checkRestaurantOwner(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.OWNER);
    }

    public boolean checkRestaurantAdmin(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.ADMIN);
    }

    public boolean checkRestaurantManager(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.MANAGER);
    }

    public boolean checkRestaurantOrderHandler(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.ORDER_HANDLER);
    }

    public boolean checkOrderOwner(long orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && order.getUser().getUserId() == currentUserDetails.getUserId();
    }

    public boolean checkOrderHandler(long orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        return order != null && checkRestaurantOrderHandler(order.getRestaurant().getRestaurantId());
    }

    public boolean checkOrderOwnerOrHandler(long orderId) {
        OrderItemless order = orderService.getByIdExcludeItems(orderId).orElse(null);
        PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && (order.getUser().getUserId() == currentUserDetails.getUserId() || checkRestaurantOrderHandler(order.getRestaurant().getRestaurantId()));
    }
}
