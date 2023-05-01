package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import ar.edu.itba.paw.webapp.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessValidator {

    @Autowired
    OrderService orderService;

    public boolean checkEditRestaurant(HttpServletRequest request, int id) {
        // TODO: Implement. This is just a placeholder.
        return id % 2 == 0;
    }

    public boolean checkOrderOwner(HttpServletRequest request, int id) {
        Order order = orderService.getById(id).orElseThrow(OrderNotFoundException::new);
        return order.getUser().getEmail().equals(ControllerUtils.getCurrentUserEmail());
    }
}
