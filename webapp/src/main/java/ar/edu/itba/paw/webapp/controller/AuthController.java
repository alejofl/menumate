package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.VerificationTokenDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.RegisterForm;
import ar.edu.itba.paw.webapp.form.EmailForm;
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
    private VerificationTokenDao verificationService;

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
        String token = verificationService.generateToken(user.getUserId());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        emailService.sendUserVerificationEmail(baseUrl, user.getEmail(), token);
        return new ModelAndView("redirect:/auth/login?verify=emailed");
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ModelAndView loginForm(@RequestParam(value = "verify", required = false) String verify) {
        return new ModelAndView("auth/login").addObject("verify", verify);
    }

    @RequestMapping(value = "/auth/verify", method = RequestMethod.GET)
    public ModelAndView verifyForm(
            @RequestParam(value = "token", required = false) @Length(min = 8, max = 8) final String token,
            @ModelAttribute("emailForm") final EmailForm emailForm
    ) {
        if(token!=null){
            if (verificationService.verifyAndDeleteToken(token))
                return new ModelAndView("redirect:/auth/login?verify=verified");
            return new ModelAndView("redirect:/auth/register?error=invalid_token_or_user"); // FIXME : invalid url
        } else {
            return new ModelAndView("auth/email_form")
                    .addObject("type", "Verify")
                    .addObject("url", "/auth/verify");
        }
    }

    @RequestMapping(value = "/auth/verify", method = RequestMethod.POST)
    public ModelAndView sendCode(
            @Valid @ModelAttribute("emailForm") final EmailForm emailForm,
            final BindingResult errors
    ){
        if (errors.hasErrors()) {
            return verifyForm(null, emailForm);
        }
        User user;
        try{
            user = userService.getByEmail(emailForm.getEmail()).orElseThrow(UserNotFoundException::new);
        } catch (UserNotFoundException e) {
            return new ModelAndView("redirect:/auth/login?verify=emailed");
        }

        if(user.getIsActive()){
            return new ModelAndView("redirect:/auth/login?verify=emailed");
        }

        String token = verificationService.generateToken(user.getUserId());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        try {
            emailService.sendUserVerificationEmail(baseUrl, user.getEmail(), token);
        } catch (MessagingException e) {
            return new ModelAndView("redirect:/auth/login?verify=error");
        }
        return new ModelAndView("redirect:/auth/login?verify=emailed");
    }
}
