package ar.edu.itba.paw.model;

public enum RestaurantTags {
    ELEGANT("Elegant"),
    CASUAL("Casual"),
    CHEAP("Cheap"),
    COSY("Cosy"),
    FAMILYFRIENDLY("Family Friendly"),
    KIDFRIENDLY("Kid Friendly"),
    LGBTFRIENDLY("LGBT Friendly"),
    PETFRIENDLY("Pet Friendly");

    private final String messageCode;

    RestaurantTags(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final RestaurantTags[] VALUES = RestaurantTags.values();

    /**
     * Gets the RestaurantTags value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantTags fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}