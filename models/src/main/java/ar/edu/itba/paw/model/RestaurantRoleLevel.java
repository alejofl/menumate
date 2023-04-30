package ar.edu.itba.paw.model;

/**
 * Represents the possible roles a user can have for a restaurant. Not included is the role of owner, as there is
 * forcedly a single owner per restaurant and it's included in the restaurants table.
 *
 * Roles are incremental, so all ADMINs are also ORDER_HANDLERs. In the database, roles are represented by this enum's
 * ordinal, so the lower the number the more permissions the role has.
 */
public enum RestaurantRoleLevel {

    /** Can edit the restaurant and handle orders. */
    ADMIN(),

    /** Cannot edit the restaurant, but can view and edit orders (e.g. mark as delivered). */
    ORDER_HANDLER()
}
