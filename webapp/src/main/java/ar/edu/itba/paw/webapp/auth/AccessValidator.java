package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RestaurantRoleService;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestaurantRoleService restaurantRoleService;

    public boolean checkIsUser(long userId) {
        final Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        return currentUserId != null && currentUserId == userId;
    }

    public boolean checkRestaurantRole(long restaurantId, RestaurantRoleLevel minimumRoleLevel) {
        final Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
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
        final Order order = orderService.getById(orderId).orElse(null);
        final PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && order.getUserId() == currentUserDetails.getUserId();
    }

    public boolean checkOrderHandler(long orderId) {
        final Order order = orderService.getById(orderId).orElse(null);
        return order != null && checkRestaurantOrderHandler(order.getRestaurantId());
    }

    public boolean checkOrderOwnerOrHandler(long orderId) {
        final Order order = orderService.getById(orderId).orElse(null);
        final PawAuthUserDetails currentUserDetails = ControllerUtils.getCurrentUserDetailsOrNull();
        return order != null && currentUserDetails != null && (order.getUserId() == currentUserDetails.getUserId() || checkRestaurantOrderHandler(order.getRestaurantId()));
    }

    public boolean checkCanListOrders(Long userId, Long restaurantId) {
        final Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        if (currentUserId == null)
            return false;

        // Either the userId is yours, or you have order_handler role at the restaurant.
        // Note: if both userId and restaurantId are null, this will return false.
        return (currentUserId.equals(userId)) || (restaurantId != null && restaurantRoleService.doesUserHaveRole(currentUserId, restaurantId, RestaurantRoleLevel.ORDER_HANDLER));
    }
}
