package ar.edu.itba.paw.model;

/**
 * Represents the possible roles a user can have.
 * Roles are incremental, and they are represented by this enum's ordinal.
 */
public enum UserRoleLevel {

    /**
     * Has full delete permissions over the application.
     */
    MODERATOR("ROLE_MODERATOR");

    private final String levelName;

    UserRoleLevel(final String role) {
        this.levelName = role;
    }

    public String getLevelName() {
        return levelName;
    }

    private static final UserRoleLevel[] VALUES = UserRoleLevel.values();

    /**
     * Gets the UserRoleLevel value by ordinal if it exists, or null otherwise.
     */
    public static UserRoleLevel fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}

