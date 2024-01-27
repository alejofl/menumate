package ar.edu.itba.paw.webapp;

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
    public static final String APPLICATION_RESTAURANT_PRODUCT_CATEGORY = "application/vnd.menumate.restaurant.product.newCategory.v1+json";
    public static final String APPLICATION_RESTAURANT_PROMOTIONS = "application/vnd.menumate.restaurant.promotions.v1+json";
    public static final String APPLICATION_RESTAURANT_REPORTS = "application/vnd.menumate.restaurant.reports.v1+json";
    public static final String APPLICATION_RESTAURANTS_UNHANDLED_REPORTS = "application/vnd.menumate.restaurantUnhandledReports.v1+json";
    public static final String APPLICATION_RESTAURANT_ACTIVATE = "application/vnd.menumate.restaurants.activate.v1+json";

    /**
     * Restaurant employee controller
     */
    public static final String APPLICATION_RESTAURANT_EMPLOYEE = "application/vnd.menumate.restaurant.employee.v1+json";

    /**
     * Review controller
     */
    public static final String APPLICATION_REVIEW = "application/vnd.menumate.reviews.v1+json";
    public static final String APPLICATION_REPLY_REVIEW = "application/vnd.menumate.reply.review.v1+json";

    /**
     * User controller
     */
    public static final String APPLICATION_USER = "application/vnd.menumate.user.v1+json";
    public static final String APPLICATION_USER_ADDRESS = "application/vnd.menumate.user.address.v1+json";
    public static final String APPLICATION_USER_ROLE = "application/vnd.menumate.userRole.v1+json";
    public static final String APPLICATION_USER_PASSWORD = "application/vnd.menumate.userPassword.v1+json";;

    private CustomMediaType() {
        throw new AssertionError();
    }
}
