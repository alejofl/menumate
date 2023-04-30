package ar.edu.itba.paw.model;

/**
 * Represents the possible roles a user can have for a restaurant.
 * Roles are incremental, so all ADMINs are also ORDER_HANDLERs. Roles are represented by this enum's ordinal, so the
 * lower the number the more permissions the role has.
 */
public enum RestaurantRoleLevel {
    /** Has full permissions over the restaurant. */
    OWNER(),

    /** Can edit the restaurant and assign roles. */
    ADMIN(),

    /** Cannot edit the restaurant, but can assign handler roles. */
    MANAGER(),

    /** Can view and handle orders (e.g. mark as delivered). */
    ORDER_HANDLER()
}
