package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("home/index");

        final List<Restaurant> restaurants = restaurantService.getAll();
        mav.addObject("restaurants", restaurants);

        return mav;
    }

    @RequestMapping(value = "/auth/register", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("auth/register");
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView errorView() {
        return new ModelAndView("errors/error");
    }

    @RequestMapping(value = "/auth/register", method = RequestMethod.POST)
    public ModelAndView register(
            @RequestParam(value = "email", required = true) final String email,
            @RequestParam(value = "password", required = true) final String password,
            @RequestParam(value = "name", required = true) final String name
    ) {
        final User user = userService.create(email, password, name);
        final ModelAndView mav = new ModelAndView("helloworld/profile");
        mav.addObject("user", user);
        return mav;
    }
}
