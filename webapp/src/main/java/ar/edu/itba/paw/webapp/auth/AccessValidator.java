package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RestaurantRoleService;
import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestaurantRoleService restaurantRoleService;

    public boolean checkIsUser(long userId) {
        Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        return currentUserId != null && currentUserId == userId;
    }

    public boolean checkRestaurantRole(long restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        if (currentUserId == null)
            return false;

        return restaurantRoleService.doesUserHaveRole(currentUserId, restaurantId, minimumRoleLevel);
    }

    public boolean checkRestaurantOwner(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.OWNER);
    }

    public boolean checkRestaurantAdmin(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.ADMIN);
    }

    public boolean checkRestaurantOrderHandler(long restaurantId) {
        return checkRestaurantRole(restaurantId, RestaurantRoleLevel.ORDER_HANDLER);
    }

    public boolean checkOrderOwner(long orderId) {
        Order order = orderService.getById(orderId).orElse(null);
        PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && order.getUserId() == currentUserDetails.getUserId();
    }

    public boolean checkOrderHandler(long orderId) {
        Order order = orderService.getById(orderId).orElse(null);
        return order != null && checkRestaurantOrderHandler(order.getRestaurantId());
    }

    public boolean checkOrderOwnerOrHandler(long orderId) {
        Order order = orderService.getById(orderId).orElse(null);
        PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && (order.getUserId() == currentUserDetails.getUserId() || checkRestaurantOrderHandler(order.getRestaurantId()));
    }
}
