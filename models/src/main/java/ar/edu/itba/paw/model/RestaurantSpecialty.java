package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum RestaurantSpecialty {

    AMERICAN("american"),
    ARGENTINE("argentine"),
    ASIAN("asian"),
    BARBECUE("barbecue"),
    BREAKFAST("breakfast"),
    CAFE("cafe"),
    CHINESE("chinese"),
    DESSERTS("desserts"),
    EUROPEAN("european"),
    FAST_FOOD("fast_food"),
    FRENCH("french"),
    GLUTEN_FREE("gluten_free"),
    GRILL("grill"),
    GREEK("greek"),
    HAMBURGERS("hamburgers"),
    ICE_CREAM("ice_cream"),
    INDIAN("indian"),
    ITALIAN("italian"),
    JAPANESE("japanese"),
    KOREAN("korean"),
    LATIN("latin"),
    MEDITERRANEAN("mediterranean"),
    MEXICAN("mexican"),
    MIDDLE_EASTERN("middle_eastern"),
    PIZZA("pizza"),
    SEAFOOD("seafood"),
    SUSHI("sushi"),
    STEAKHOUSE("steakhouse"),
    THAI("thai"),
    TURKISH("turkish"),
    VEGAN("vegan"),
    VEGETARIAN("vegetarian");

    private final String messageCode;

    RestaurantSpecialty(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final RestaurantSpecialty[] VALUES = RestaurantSpecialty.values();

    private static final Map<String, RestaurantSpecialty> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    /**
     * Gets the RestaurantSpecialty value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantSpecialty fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static RestaurantSpecialty fromCode(String code) {
        return VALUES_BY_CODE.get(code);
    }
}
