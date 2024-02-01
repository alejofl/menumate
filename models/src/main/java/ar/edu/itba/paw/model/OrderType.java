package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final Map<String, OrderType> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    /**
     * Gets the OrderType value by ordinal if it exists, or null otherwise.
     */
    public static OrderType fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static OrderType fromCode(String code) {
        return code == null ? null : VALUES_BY_CODE.get(code.trim().toLowerCase());
    }
}