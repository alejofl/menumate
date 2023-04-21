package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("home/index");

        final List<Restaurant> restaurants = restaurantService.getAll();
        mav.addObject("restaurants", restaurants);

        return mav;
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView errorView() {
        return new ModelAndView("errors/error");
    }
}
