package ar.edu.itba.paw.webapp.api;

public class CustomMediaType {

    /***
     * Image controller
     */
    public static final String APPLICATION_IMAGE = "application/vnd.menumate.images.v1+json";

    /***
     * Order controller
     */
    public static final String APPLICATION_ORDERS = "application/vnd.menumate.orders.v1+json";
    public static final String APPLICATION_ORDER_ITEMS = "application/vnd.menumate.order.items.v1+json";

    /**
     * Restaurant controller
     */
    public static final String APPLICATION_RESTAURANT = "application/vnd.menumate.restaurants.v1+json";
    public static final String APPLICATION_RESTAURANT_DETAILS = "application/vnd.menumate.restaurantDetails.v1+json";
    public static final String APPLICATION_RESTAURANT_CATEGORIES = "application/vnd.menumate.restaurant.categories.v1+json";
    public static final String APPLICATION_RESTAURANT_PRODUCTS = "application/vnd.menumate.restaurant.products.v1+json";
    public static final String APPLICATION_RESTAURANT_PROMOTIONS = "application/vnd.menumate.restaurant.promotions.v1+json";
    public static final String APPLICATION_RESTAURANT_REPORTS = "application/vnd.menumate.restaurant.reports.v1+json";
    public static final String APPLICATION_RESTAURANTS_UNHANDLED_REPORTS = "application/vnd.menumate.restaurantUnhandledReports.v1+json";
    public static final String USER_ROLE_V1 = "application/vnd.menumate.userRole.v1+json";
    public static final String APPLICATION_USER_RESETS_PASSWORD = "application/vnd.menumate.userResetsPassword.v1+json";;

    private CustomMediaType() {
        throw new AssertionError();
    }
}
