package ar.edu.itba.paw.webapp.utils;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.UserAddress;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public final class UriUtils {
    private UriUtils() {

    }

    /**
     * Returns a URI pointing to the given imageId, or null if imageId is null.
     */
    public static URI getImageUri(final UriInfo uriInfo, final Long imageId) {
        if (imageId == null)
            return null;
        return uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(imageId)).build();
    }

    public static URI getRestaurantUri(final UriInfo uriInfo, final long restaurantId) {
        return uriInfo.getBaseUriBuilder().path("restaurants").path(String.valueOf(restaurantId)).build();
    }

    public static URI getRestaurantCategoriesUri(final UriInfo uriInfo, final long restaurantId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants").path(String.valueOf(restaurantId))
                .path("categories")
                .build();
    }

    public static URI getCategoryUri(final UriInfo uriInfo, final long restaurantId, final long categoryId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants").path(String.valueOf(restaurantId))
                .path("categories").path(String.valueOf(categoryId))
                .build();
    }

    public static URI getCategoryUri(final UriInfo uriInfo, final Category category) {
        return getCategoryUri(uriInfo, category.getRestaurantId(), category.getCategoryId());
    }

    public static URI getCategoryProductsUri(final UriInfo uriInfo, final long restaurantId, final long categoryId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants").path(String.valueOf(restaurantId))
                .path("categories").path(String.valueOf(categoryId))
                .path("products")
                .build();
    }

    public static URI getCategoryProductsUri(final UriInfo uriInfo, final Category category) {
        return getCategoryProductsUri(uriInfo, category.getRestaurantId(), category.getCategoryId());
    }

    public static URI getProductUri(final UriInfo uriInfo, final long restaurantId, final long categoryId, final long productId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants").path(String.valueOf(restaurantId))
                .path("categories").path(String.valueOf(categoryId))
                .path("products").path(String.valueOf(productId))
                .build();
    }

    public static URI getProductUri(final UriInfo uriInfo, final Product product) {
        return getProductUri(uriInfo, product.getCategory().getRestaurantId(), product.getCategoryId(), product.getProductId());
    }

    public static URI getUserUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(userId)).build();
    }

    public static URI getUserEmployedAtUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants")
                .queryParam("forEmployeeId", String.valueOf(userId))
                .build();
    }

    public static URI getUserAddressesUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder()
                .path("users").path(String.valueOf(userId))
                .path("addresses")
                .build();
    }

    public static URI getUserAddressUri(final UriInfo uriInfo, final long userId, final long addressId) {
        return uriInfo.getBaseUriBuilder()
                .path("users").path(String.valueOf(userId))
                .path("addresses").path(String.valueOf(addressId))
                .build();
    }

    public static URI getUserAddressUri(final UriInfo uriInfo, final UserAddress address) {
        return getUserAddressUri(uriInfo, address.getUserId(), address.getAddressId());
    }

    public static URI getOrderUri(final UriInfo uriInfo, final long orderId) {
        return uriInfo.getBaseUriBuilder().path("orders").path(String.valueOf(orderId)).build();
    }

    public static URI getOrderItemsUri(final UriInfo uriInfo, final long orderId) {
        return uriInfo.getBaseUriBuilder()
                .path("orders").path(String.valueOf(orderId))
                .path("items")
                .build();
    }

    public static URI getOrdersByUserUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path("orders").queryParam("userId", String.valueOf(userId)).build();
    }

    public static URI getOrdersByRestaurantUri(final UriInfo uriInfo, final long restaurantId) {
        return uriInfo.getBaseUriBuilder().path("orders").queryParam("restaurantId", String.valueOf(restaurantId)).build();
    }

    public static URI getReviewsByUserUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path("reviews").queryParam("userId", String.valueOf(userId)).build();
    }

    public static URI getReviewsByRestaurantUri(final UriInfo uriInfo, final long restaurantId) {
        return uriInfo.getBaseUriBuilder().path("reviews").queryParam("restaurantId", String.valueOf(restaurantId)).build();
    }

    public static URI getReviewUri(final UriInfo uriInfo, final long reviewId) {
        return uriInfo.getBaseUriBuilder().path("reviews").path(String.valueOf(reviewId)).build();
    }

    public static URI getRestaurantEmployeesUri(final UriInfo uriInfo, final long restaurantId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants").path(String.valueOf(restaurantId))
                .path("employees")
                .build();
    }

    public static URI getRestaurantEmployeeUri(final UriInfo uriInfo, final long restaurantId, final long userId) {
        return uriInfo.getBaseUriBuilder()
                .path("restaurants").path(String.valueOf(restaurantId))
                .path("employees").path(String.valueOf(userId))
                .build();
    }
}
