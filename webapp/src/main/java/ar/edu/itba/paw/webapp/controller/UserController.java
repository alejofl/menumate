package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private OrderService orderService;
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView myOrders() {
        ModelAndView mav = new ModelAndView("user/myorders");
        return mav;
    }

    @RequestMapping(value = "/restaurants/order", method = RequestMethod.GET)
    public ModelAndView restaurantOrder() {

        List<Order> orders = orderService.getByOrderTypeAndRestaurant(OrderType.DINE_IN, 3);
        orders.addAll(orderService.getByOrderTypeAndRestaurant(OrderType.TAKEAWAY, 3));
        orders.addAll(orderService.getByOrderTypeAndRestaurant(OrderType.DELIVERY, 3));
        orders.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getDateOrdered().compareTo(o1.getDateOrdered());
            }
        });
        ModelAndView mav = new ModelAndView("menu/restaurant_orders");
        mav.addObject("orders", orders);
        return mav;
    }
}

