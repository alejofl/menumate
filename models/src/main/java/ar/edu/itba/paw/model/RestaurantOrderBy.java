package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum RestaurantOrderBy {
    ALPHABETIC("alphabetic"),
    RATING("rating"),
    PRICE("price"),
    DATE("date");

    private final String messageCode;

    RestaurantOrderBy(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final RestaurantOrderBy[] VALUES = RestaurantOrderBy.values();

    private static final Map<String, RestaurantOrderBy> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    /**
     * Gets the RestaurantOrderBy value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantOrderBy fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static RestaurantOrderBy fromCode(String code) {
        return VALUES_BY_CODE.get(code.trim().toLowerCase());
    }
}
