package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.form.EmailForm;
import ar.edu.itba.paw.webapp.form.RegisterForm;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Controller
public class AuthController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

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

        try {
            userService.createOrConsolidate(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        } catch (MessagingException e) {
            LOGGER.error("User Registered Email Sending Failed");
            return new ModelAndView("redirect:/auth/login?error=mailer_error");
        }
        return new ModelAndView("redirect:/auth/login?type=verify-emailed");
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ModelAndView loginForm(@RequestParam(value = "type", required = false) String type) {
        return new ModelAndView("auth/login").addObject("type", type);
    }

    @RequestMapping(value = {"/auth/verify"}, method = RequestMethod.GET)
    public ModelAndView getVerifyEmailForm(
            @RequestParam(value = "token", required = false) @Length(min = 32, max = 32) final String token,
            @ModelAttribute("emailForm") final EmailForm emailForm
    ) {
        if (token == null) {
            return new ModelAndView("auth/email_form")
                    .addObject("actionType", "verify")
                    .addObject("url", "/auth/verify");
        }

        if (userService.verifyUserAndDeleteVerificationToken(token))
            return new ModelAndView("redirect:/");
        else
            return new ModelAndView("redirect:/auth/login?error=request-error");
    }

    @RequestMapping(value = "/auth/verify", method = RequestMethod.POST)
    public ModelAndView sendVerifyToken(
            @Valid @ModelAttribute("emailForm") final EmailForm emailForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors())
            return getVerifyEmailForm(null, emailForm);

        try {
            User user = userService.getByEmail(emailForm.getEmail()).orElseThrow(UserNotFoundException::new);
            userService.sendUserVerificationToken(user);
            return new ModelAndView("redirect:/auth/login?type=verify-emailed");
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found when verifying account");
            return new ModelAndView("redirect:/auth/login?type=verify-emailed");
        } catch (MessagingException e) {
            LOGGER.error("Verify Token Email Sending Failed");
            return new ModelAndView("redirect:/auth/login?error=mailer_error");
        }
    }

    @RequestMapping(value = {"/auth/reset-password"}, method = RequestMethod.GET)
    public ModelAndView getResetPasswordEmailForm(
            @RequestParam(value = "token", required = false) @Length(min = 32, max = 32) final String token,
            @ModelAttribute("emailForm") final EmailForm emailForm
    ) {
        if (token == null) {
            return new ModelAndView("auth/email_form")
                    .addObject("actionType", "reset-password")
                    .addObject("url", "/auth/reset-password");
        }

        if (userService.isValidResetPasswordToken(token))
            return new ModelAndView("redirect:/auth/reset-password-form?token=" + token);
        else
            return new ModelAndView("redirect:/auth/login?error=request-error");
    }

    @RequestMapping(value = "/auth/reset-password", method = RequestMethod.POST)
    public ModelAndView sendResetPasswordToken(
            @Valid @ModelAttribute("emailForm") final EmailForm emailForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors())
            return getResetPasswordEmailForm(null, emailForm);

        try {
            User user = userService.getByEmail(emailForm.getEmail()).orElseThrow(UserNotFoundException::new);
            userService.sendPasswordResetToken(user);
            return new ModelAndView("redirect:/auth/login?type=reset-password-emailed");
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found when resetting password");
            return new ModelAndView("redirect:/auth/login?type=reset-password-emailed");
        } catch (MessagingException e) {
            LOGGER.error("Reset Password Email Sending Failed");
            return new ModelAndView("redirect:/auth/login?error=mailer_error");
        }
    }

    @RequestMapping(value = "/auth/reset-password-form", method = RequestMethod.GET)
    public ModelAndView resetPasswordForm(
            @RequestParam(value = "token", required = false) @Length(min = 32, max = 32) final String token,
            @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm
    ) {
        if (token != null && userService.isValidResetPasswordToken(token))
            return new ModelAndView("auth/reset_password").addObject("token", token);
        else
            return new ModelAndView("redirect:/auth/login?error=mailer_error");
    }

    @RequestMapping(value = "/auth/reset-password-form", method = RequestMethod.POST)
    public ModelAndView resetPassword(
            @RequestParam(value = "token", required = true) @Length(min = 32, max = 32) final String token,
            @Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors())
            return resetPasswordForm(token, resetPasswordForm);

        if (userService.updatePasswordAndDeleteResetPasswordToken(token, resetPasswordForm.getPassword())) {
            return new ModelAndView("redirect:/auth/login?type=reset-password-success");
        } else {
            return new ModelAndView("redirect:/auth/login?error=password-reset-error");
        }
    }
}
