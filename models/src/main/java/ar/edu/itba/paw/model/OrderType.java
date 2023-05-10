package ar.edu.itba.paw.model;

public enum OrderType {
    DINE_IN("dinein"),
    TAKEAWAY("takeaway"),
    DELIVERY("delivery");

    private final String messageCode;

    OrderType(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final OrderType[] VALUES = OrderType.values();

    /**
     * Gets the OrderType value by ordinal if it exists, or null otherwise.
     */
    public static OrderType fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}