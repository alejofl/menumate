package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ControllerHelper {

    @Autowired
    private UserService userService;

    @ModelAttribute("currentUser")
    public User addCurrentUser() {
        final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails instanceof PawAuthUserDetails) {
            PawAuthUserDetails pawAuthUserDetails = (PawAuthUserDetails) userDetails;
            String email = pawAuthUserDetails.getUsername();
            return userService.getByEmail(email).orElse(null);
        }

        return null;
    }
}
