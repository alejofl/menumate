/* eslint-disable no-magic-numbers */

export const BAD_REQUEST_STATUS_CODE = 400;
export const UNAUTHORIZED_STATUS_CODE = 401;

export const IMAGE_CONTENT_TYPE = "application/vnd.menumate.images.v1+json";
export const ORDERS_CONTENT_TYPE = "application/vnd.menumate.orders.v1+json";
export const ORDER_ITEMS_CONTENT_TYPE = "application/vnd.menumate.order.items.v1+json";
export const RESTAURANTS_CONTENT_TYPE = "application/vnd.menumate.restaurants.v1+json";
export const RESTAURANT_DETAILS_CONTENT_TYPE = "application/vnd.menumate.restaurantDetails.v1+json";
export const RESTAURANT_CATEGORIES_CONTENT_TYPE = "application/vnd.menumate.restaurant.categories.v1+json";
export const RESTAURANT_PRODUCTS_CONTENT_TYPE = "application/vnd.menumate.restaurant.products.v1+json";
export const RESTAURANT_PROMOTIONS_CONTENT_TYPE = "application/vnd.menumate.restaurant.promotions.v1+json";
export const RESTAURANT_EMPLOYEES_CONTENT_TYPE = "application/vnd.menumate.restaurant.employee.v1+json";
export const REPORTS_CONTENT_TYPE = "application/vnd.menumate.restaurant.reports.v1+json";
export const UNHANDLED_REPORTS_CONTENT_TYPE = "application/vnd.menumate.restaurantUnhandledReports.v1+json";
export const REVIEW_CONTENT_TYPE = "application/vnd.menumate.reviews.v1+json";
export const REVIEW_REPLY_CONTENT_TYPE = "application/vnd.menumate.reply.review.v1+json";
export const USER_CONTENT_TYPE = "application/vnd.menumate.user.v1+json";
export const USER_ADDRESS_CONTENT_TYPE = "application/vnd.menumate.user.address.v1+json";
export const USER_ROLE_CONTENT_TYPE = "application/vnd.menumate.userRole.v1+json";
export const USER_PASSWORD_CONTENT_TYPE = "application/vnd.menumate.userPassword.v1+json";

export const EMAIL_ALREADY_IN_USE_ERROR = {"message":"Email already in use!", "path":"registerUser.arg0.email"};

export const WAIT_TIME = 500;

export const GET_LANGUAGE_CODE = (language) => language.substring(0, 3);
