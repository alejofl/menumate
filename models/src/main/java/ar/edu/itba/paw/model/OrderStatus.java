package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    public boolean isInProgress() {
        return !this.isCancelledOrRejected() && this != OrderStatus.DELIVERED;
    }

    private static final OrderStatus[] VALUES = OrderStatus.values();

    private static final Map<String, OrderStatus> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    /**
     * Gets the OrderStatus value by ordinal if it exists, or null otherwise.
     */
    public static OrderStatus fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static OrderStatus fromCode(String code) {
        return VALUES_BY_CODE.get(code);
    }
}
