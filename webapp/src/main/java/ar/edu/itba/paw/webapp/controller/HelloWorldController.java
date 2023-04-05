package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class HelloWorldController {
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public HelloWorldController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("home/index");
    }

    @RequestMapping("/restaurant/{id}")
    public ModelAndView restaurantMenu(@PathVariable String id) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_menu");
        mav.addObject("restaurant_name", "Atuel");
        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("helloworld/register");
    }

    @RequestMapping(value = "/email")
    public void email() {
        emailService.sendEmail("ivan.chayer@sabf.org.ar", "Hello", "Hello World");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(
            @RequestParam(value = "username", required = true) final String username,
            @RequestParam(value = "password", required = true) final String password
    ) {
        final User user = userService.create(username, password, "ivan.chayer@sabf.org.ar");
        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("user", user);
        return mav;
    }
}
