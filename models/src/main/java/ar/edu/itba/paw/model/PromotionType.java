package ar.edu.itba.paw.model;

public enum PromotionType {
    INSTANT,
    SCHEDULED;

    private static final PromotionType[] VALUES = PromotionType.values();

    /**
     * Gets the PromotionType value by ordinal if it exists, or null otherwise.
     */
    public static PromotionType fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}
