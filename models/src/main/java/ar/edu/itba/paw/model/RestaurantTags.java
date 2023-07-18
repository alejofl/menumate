package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum RestaurantTags {
    ELEGANT("elegant"),
    CASUAL("casual"),
    CHEAP("cheap"),
    COSY("cosy"),
    FAMILY_FRIENDLY("family_friendly"),
    KID_FRIENDLY("kid_friendly"),
    LGBT_FRIENDLY("lgbt_friendly"),
    PET_FRIENDLY("pet_friendly"),
    HEALTHY("healthy"),
    SUSTAINABLE("sustainable"),
    LOCALLY_SOURCED("locally_sourced"),
    HISTORIC("historic"),
    TRENDY("trendy"),
    ROMANTIC("romantic"),
    OUTDOOR_DINING("outdoor_dining"),
    FINE_DINING("fine_dining"),
    INTERNATIONAL("international"),
    WHEELCHAIR_ACCESSIBLE("wheelchair_accessible"),
    LIVE_MUSIC("live_music"),
    KARAOKE("karaoke"),
    HAPPY_HOUR("happy_hour"),
    OLD_FASHIONED("old_fashioned");

    private final String messageCode;

    RestaurantTags(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final RestaurantTags[] VALUES = RestaurantTags.values();

    private static final Map<String, RestaurantTags> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    /**
     * Gets the RestaurantTags value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantTags fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static RestaurantTags fromCode(String code) {
        return VALUES_BY_CODE.get(code.trim().toLowerCase());
    }
}