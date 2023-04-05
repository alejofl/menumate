package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


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

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return userCreatedError();
//        return new ModelAndView("helloworld/register");
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
        final User user = userService.createUser(username, password);
        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("user", user);
        return mav;
    }

    // Controller Based Exception Handling - Using @ExceptionHandler
    // Add @ExceptionHandler methods to any controller to specifically handle exceptions thrown
    // by request handling @RequestMapping methods in the same controller.
    @ExceptionHandler(Exception.class)
    public ModelAndView userCreatedError() {
        ModelAndView mav = new ModelAndView("errors/error");
//        mav.addObject("exception", ex);
//        mav.addObject("url", req.getRequestURL());
        return mav;
    }

}
