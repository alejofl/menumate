package ar.edu.itba.paw.model;

public enum OrderStatus {
    PENDING("pending"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    CONFIRMED("confirmed"),
    READY("ready"),
    DELIVERED("delivered");

    private final String messageCode;

    OrderStatus(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public boolean happensBefore(OrderStatus status) {
        return this.ordinal() < status.ordinal();
    }

    public boolean happensBeforeOrIs(OrderStatus status) {
        return this.ordinal() <= status.ordinal();
    }

    public boolean isCancelledOrRejected() {
        return this == OrderStatus.CANCELLED || this == OrderStatus.REJECTED;
    }

    private static final OrderStatus[] VALUES = OrderStatus.values();

    /**
     * Gets the OrderStatus value by ordinal if it exists, or null otherwise.
     */
    public static OrderStatus fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}
