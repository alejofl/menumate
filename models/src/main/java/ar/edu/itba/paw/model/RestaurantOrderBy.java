package ar.edu.itba.paw;

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

    /**
     * Gets the RestaurantOrderBy value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantOrderBy fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}
