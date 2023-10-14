package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the possible roles a user can have.
 * Roles are incremental, and they are represented by this enum's ordinal.
 */
public enum UserRoleLevel {

    /**
     * Has full delete permissions over the application.
     */
    MODERATOR("ROLE_MODERATOR");

    private final String messageCode;

    UserRoleLevel(final String role) {
        this.messageCode = role;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final UserRoleLevel[] VALUES = UserRoleLevel.values();

    /**
     * Gets the UserRoleLevel value by ordinal if it exists, or null otherwise.
     */
    public static UserRoleLevel fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    private static final Map<String, UserRoleLevel> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    public static UserRoleLevel fromCode(String code) {
        return code == null ? null : VALUES_BY_CODE.get(code.trim().toUpperCase());
    }
}

