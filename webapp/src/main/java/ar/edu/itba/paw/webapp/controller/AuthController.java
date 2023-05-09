package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.VerificationService;
import ar.edu.itba.paw.webapp.form.RegisterForm;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private EmailService emailService;


    @RequestMapping(value = "/auth/register", method = RequestMethod.GET)
    public ModelAndView registerForm(@ModelAttribute("registerForm") final RegisterForm registerForm) {
        return new ModelAndView("auth/register");
    }

    @RequestMapping(value = "/auth/register", method = RequestMethod.POST)
    public ModelAndView register(
            @Valid @ModelAttribute("registerForm") final RegisterForm registerForm,
            final BindingResult errors
    ) throws MessagingException {
        if (errors.hasErrors()) {
            return registerForm(registerForm);
        }

        final User user = userService.create(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        // FIXME: use user_id
        String token = verificationService.generateVerificationToken(user.getEmail());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        emailService.sendUserVerificationEmail(baseUrl, user.getEmail(), token);
        return new ModelAndView("redirect:/auth/login?verify=emailed");
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ModelAndView loginForm(@RequestParam(value = "verify", required = false) String verify) {
        ModelAndView model = new ModelAndView("auth/login");
        model.addObject("verify", verify);
        return model;
    }

    @RequestMapping(value = "/auth/verify", method = RequestMethod.GET)
    public ModelAndView verifyUser(
            @RequestParam(value = "token", required = true) @Length(min = 8, max = 8) final String token
    ) {
        if (verificationService.verifyAndDeleteToken(token))
            return new ModelAndView("redirect:/auth/login?verify=verified");
        return new ModelAndView("redirect:/auth/register?error=invalid_token_or_user");
    }
}
