package ar.edu.itba.paw.webapp.api;

public class CustomMediaType {

    public static final String USER_ROLE_V1 = "application/vnd.menumate.userRole.v1+json";
    public static final String APPLICATION_USER_RESETS_PASSWORD = "application/vnd.menumate.userResetsPassword.v1+json";;
    public static final String APPLICATION_LISTS_RESTAURANTS_REPORTS_COUNT = "application/vnd.menumate.restaurantWithUnhandledReports.v1+json";
    public static final String APPLICATION_LISTS_RESTAURANT_REPORTS = "application/vnd.menumate.restaurantReports.v1+json";
    public static final String APPLICATION_RETURNS_RESTAURANT_DETAILS = "application/vnd.menumate.restaurantDetails.v1+json";;

    private CustomMediaType() {
        throw new AssertionError();
    }
}
