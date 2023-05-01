package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

public class ControllerUtils {
    /**
     * Returns the currently logged-in user's email, or null of no user is logged in.
     */
    public static String getCurrentUserEmail() {
        final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails instanceof PawAuthUserDetails) {
            PawAuthUserDetails pawAuthUserDetails = (PawAuthUserDetails) userDetails;
            return pawAuthUserDetails.getUsername();
        }

        return null;
    }

    /**
     * Gets the currently logged-in user, or null if there's no such user.
     * @param userService The UserService instance to get the user from.
     */
    public static User getCurrentUserOrNull(UserService userService) {
        return userService.getByEmail(getCurrentUserEmail()).orElse(null);
    }

    /**
     * Gets the currently logged-in user, or throws an UserNotFoundException if there's no such user.
     * @param userService The UserService instance to get the user from.
     */
    public static User getCurrentUserOrThrow(UserService userService) {
        return userService.getByEmail(getCurrentUserEmail()).orElseThrow(UserNotFoundException::new);
    }
}
