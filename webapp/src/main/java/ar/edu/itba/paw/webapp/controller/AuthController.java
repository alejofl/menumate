package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.form.RegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/auth/register", method = RequestMethod.GET)
    public ModelAndView registerForm(@ModelAttribute("registerForm") final RegisterForm registerForm) {
        return new ModelAndView("auth/register");
    }

    @RequestMapping(value = "/auth/register", method = RequestMethod.POST)
    public ModelAndView register(
            @Valid @ModelAttribute("registerForm") final RegisterForm registerForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return registerForm(registerForm);
        }

        final User user = userService.create(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        final ModelAndView mav = new ModelAndView("helloworld/profile");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ModelAndView loginForm() {
        return new ModelAndView("auth/login");
    }
}
