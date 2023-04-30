package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessValidator {
    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    public boolean checkEditRestaurant(HttpServletRequest request, int id) {
        // TODO: Implement. This is just a placeholder.
        return id % 2 == 0;
    }

    public boolean checkOrderOwner(HttpServletRequest request, int id) {
        Order order = orderService.getById(id).orElseThrow(IllegalArgumentException::new);
        User user = getCurrentUser();

        return user != null && order.getUser().getUserId() == user.getUserId();
    }

    private User getCurrentUser() {
        final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails instanceof PawAuthUserDetails) {
            PawAuthUserDetails pawAuthUserDetails = (PawAuthUserDetails) userDetails;
            String email = pawAuthUserDetails.getUsername();
            return userService.getByEmail(email).orElse(null);
        }

        return null;
    }
}
