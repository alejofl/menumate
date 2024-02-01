package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PromotionType {
    INSTANT("instant"),
    SCHEDULED("scheduled");

    private final String messageCode;

    PromotionType(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final PromotionType[] VALUES = PromotionType.values();

    private static final Map<String, PromotionType> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));


    /**
     * Gets the PromotionType value by ordinal if it exists, or null otherwise.
     */
    public static PromotionType fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static PromotionType fromCode(String code) {
        return code == null ? null : VALUES_BY_CODE.get(code.trim().toLowerCase());
    }
}
