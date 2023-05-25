package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the possible roles a user can have for a restaurant.
 * Roles are incremental, so all ADMINs are also ORDER_HANDLERs. Roles are represented by this enum's ordinal, so the
 * lower the number the more permissions the role has.
 */
public enum RestaurantRoleLevel {
    /**
     * Has full permissions over the restaurant.
     */
    OWNER("owner"),

    /**
     * Can edit the restaurant and handle orders.
     */
    ADMIN("admin"),

    /**
     * Can view and handle orders (e.g. mark as delivered).
     */
    ORDER_HANDLER("orderhandler");

    private final String messageCode;

    RestaurantRoleLevel(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    /**
     * Returns whether this RestaurantRoleLevel has a certain permission level or better.
     */
    public boolean hasPermissionOf(RestaurantRoleLevel level) {
        return this.ordinal() <= level.ordinal();
    }

    private static final RestaurantRoleLevel[] VALUES = RestaurantRoleLevel.values();

    public static final List<RestaurantRoleLevel> VALUES_EXCEPT_OWNER = Arrays.asList(VALUES).subList(1, VALUES.length);

    /**
     * Gets the RestaurantRoleLevel value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantRoleLevel fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}
