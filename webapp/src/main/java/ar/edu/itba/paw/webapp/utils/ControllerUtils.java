package ar.edu.itba.paw.webapp.utils;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.core.*;
import java.util.Date;
import java.util.function.Supplier;

public final class ControllerUtils {
    public static final int DEFAULT_ORDERS_PAGE_SIZE = 20;
    public static final int DEFAULT_REVIEWS_PAGE_SIZE = 20;
    public static final int DEFAULT_REPORTS_PAGE_SIZE = 20;
    public static final int DEFAULT_SEARCH_PAGE_SIZE = 12;
    public static final int DEFAULT_RESTAURANT_PAGE_SIZE = 12;
    public static final int DEFAULT_MYRESTAURANTS_PAGE_SIZE = 20;
    public static final int MAX_RESTAURANTS_FOR_HOMEPAGE = 4;
    public static final int IMAGE_MAX_SIZE = 1024 * 1024 * 5; // 1024 B = 1 KB && 1024 B * 1024 = 1 MB ==> MAX_SIZE = 5 MB
    public static final int IMAGE_MAX_AGE = 2592000;

    private ControllerUtils() {
        throw new AssertionError();
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
        final PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        return details == null ? null : details.getUsername();
    }

    /**
     * Returns the currently logged-in user's userId, or null of no user is logged in.
     */
    public static Long getCurrentUserIdOrNull() {
        final PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        return details == null ? null : details.getUserId();
    }

    /**
     * Returns the currently logged-in user's userId, or throws an UserNotFoundException if there's no such user.
     */
    public static long getCurrentUserIdOrThrow() {
        final PawAuthUserDetails details = getCurrentUserDetailsOrNull();
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
        final PawAuthUserDetails details = getCurrentUserDetailsOrNull();
        return details == null ? null : userService.getById(details.getUserId()).orElse(null);
    }

    /**
     * Gets the currently logged-in user, or throws an UserNotFoundException if there's no such user.
     *
     * @param userService The UserService instance to get the user from.
     */
    public static User getCurrentUserOrThrow(UserService userService) {
        final PawAuthUserDetails details = getCurrentUserDetailsOrThrow();
        return userService.getById(details.getUserId()).orElseThrow(UserNotFoundException::new);
    }

    public static <T> Response.ResponseBuilder addPagingLinks(Response.ResponseBuilder response, PaginatedResult<T> paginatedResult, UriInfo uriInfo) {
        if (paginatedResult.getPageNumber() < paginatedResult.getTotalPageCount()) {
            response.link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", String.valueOf(paginatedResult.getPageNumber() + 1)).build().toString(), "next");
        }
        if (paginatedResult.getPageNumber() > 1) {
            response.link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", String.valueOf(paginatedResult.getPageNumber() - 1)).build().toString(), "prev");
        }
        response.link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", String.valueOf(1)).build().toString(), "first");
        response.link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", String.valueOf(paginatedResult.getTotalPageCount())).build().toString(), "last");
        return response;
    }

    /**
     * Cache control
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
     */

    public static Response.ResponseBuilder setMaxAge(Response.ResponseBuilder responseBuilder, int maxAge) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAge);
        return responseBuilder.cacheControl(cacheControl);
    }

    // https://howtodoinjava.com/resteasy/jax-rs-resteasy-cache-control-with-etag-example/
    public static <T> Response buildResponseUsingEtag(Request request, int hashCode, Supplier<T> dto) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        final EntityTag eTag = new EntityTag(String.valueOf(hashCode));
        Response.ResponseBuilder response = request.evaluatePreconditions(eTag);

        if (response == null) {
            response = Response.ok(dto.get()).tag(eTag);
            cacheControl.setNoStore(false);
        }

        return response.header(HttpHeaders.VARY, "Accept, Content-Type").cacheControl(cacheControl).build();
    }

    public static <T> Response buildResponseUsingLastModified(Request request, Date lastModified, Supplier<T> dto) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        Response.ResponseBuilder response = request.evaluatePreconditions(lastModified);
        if (response == null) {
            response = Response.ok(dto.get()).lastModified(lastModified);
            cacheControl.setNoStore(false);
        }

        return response.header(HttpHeaders.VARY, "Accept, Content-Type").cacheControl(cacheControl).build();
    }
}
