package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView myOrders() {
        ModelAndView mav = new ModelAndView("user/myorders");
        User currentUser = userService.getByEmail(ControllerHelper.getCurrentUserEmail()).orElse(null);
        mav.addObject("orders", orderService.getByUser(currentUser.getUserId()));
        return mav;
    }
}
