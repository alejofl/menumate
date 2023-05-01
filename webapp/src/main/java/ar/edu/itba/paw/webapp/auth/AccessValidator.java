package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import ar.edu.itba.paw.webapp.exception.OrderNotFoundException;
import ar.edu.itba.paw.webapp.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessValidator {

    @Autowired
    OrderService orderService;

    @Autowired
    RestaurantService restaurantService;

    public boolean checkRestaurantOwner(HttpServletRequest request, int id) {
        // TODO change this to use Restaurant Roles Table
        Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        return restaurant.getEmail().equals(ControllerUtils.getCurrentUserEmail());
    }

    public boolean checkOrderOwner(HttpServletRequest request, int id) {
        Order order = orderService.getById(id).orElseThrow(OrderNotFoundException::new);
        return order.getUser().getEmail().equals(ControllerUtils.getCurrentUserEmail());
    }
}
