package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class ControllerUtils {
    static final int DEFAULT_ORDERS_PAGE_SIZE = 20;
    static final int DEFAULT_SEARCH_PAGE_SIZE = 12;
    static final int DEFAULT_RESTAURANT_PAGE_SIZE = 12;

    private ControllerUtils() {

    }

    /**
     * Returns the currently logged-in user's details, or null of no user is logged in.
     */
    public static PawAuthUserDetails getCurrentUserDetailsOrNull() {
        final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails instanceof PawAuthUserDetails ? (PawAuthUserDetails) userDetails : null;
    }

    /**
     * Returns the currently logged-in user's details, or throws an UserNotFoundException if there's no such user.
     */
    public static PawAuthUserDetails getCurrentUserDetailsOrThrow() {
        final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails instanceof PawAuthUserDetails)
            return (PawAuthUserDetails) userDetails;

        throw new UserNotFoundException();
    }

    /**
     * Returns the currently logged-in user's email, or null of no user is logged in.
     */
    public static String getCurrentUserEmailOrNull() {
        PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        return details == null ? null : details.getUsername();
    }

    /**
     * Returns the currently logged-in user's userId, or null of no user is logged in.
     */
    public static Long getCurrentUserIdOrNull() {
        PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        return details == null ? null : details.getUserId();
    }

    /**
     * Returns the currently logged-in user's userId, or throws an UserNotFoundException if there's no such user.
     */
    public static long getCurrentUserIdOrThrow() {
        PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        if (details == null)
            throw new UserNotFoundException();
        return details.getUserId();
    }

    /**
     * Gets the currently logged-in user, or null if there's no such user.
     *
     * @param userService The UserService instance to get the user from.
     */
    public static User getCurrentUserOrNull(UserService userService) {
        PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        return details == null ? null : userService.getById(details.getUserId()).orElse(null);
    }

    /**
     * Gets the currently logged-in user, or throws an UserNotFoundException if there's no such user.
     *
     * @param userService The UserService instance to get the user from.
     */
    public static User getCurrentUserOrThrow(UserService userService) {
        PawAuthUserDetails details = getCurrentUserDetailsOrThrow();
        return userService.getById(details.getUserId()).orElseThrow(UserNotFoundException::new);
    }
}
