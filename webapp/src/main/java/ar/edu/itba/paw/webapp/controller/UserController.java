package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Restaurant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView myOrders() {
        ModelAndView mav = new ModelAndView("user/myorders");
        return mav;
    }
}
