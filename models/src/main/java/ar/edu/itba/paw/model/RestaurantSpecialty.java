package ar.edu.itba.paw.model;

public enum RestaurantSpecialty {

    AMERICAN("American style food"),
    ARGENTINE("Argentine style food"),
    ASIAN("Asian style food"),
    BARBECUE("Barbecue"),
    BREAKFAST("Breakfast"),
    CAFE("Cafe"),
    CHINESE("Chinese style food"),
    DESSERTS("Desserts"),
    EUROPEAN("European style food"),
    FAST_FOOD("Fast food"),
    FRENCH("French style food"),
    GLUTEN_FREE("Gluten free"),
    GRILL("Grill"),
    GREEK("Greek style food"),
    HAMBURGERS("Hamburgers"),
    ICE_CREAM("Ice cream"),
    INDIAN("Indian style food"),
    ITALIAN("Italian style food"),
    JAPANESE("Japanese style food"),
    KOREAN("Korean style food"),
    LATIN("Latin style food"),
    MEDITERRANEAN("Mediterranean style food"),
    MEXICAN("Mexican style food"),
    MIDDLE_EASTERN("Middle Eastern style food"),
    PIZZA("Pizza"),
    SEAFOOD("Seafood"),
    SUSHI("Sushi"),
    STEAKHOUSE("Steakhouse"),
    THAI("Thai style food"),
    TURKISH("Turkish style food"),
    VEGAN("Vegan"),
    VEGETARIAN("Vegetarian");

    private final String messageCode;

    RestaurantSpecialty(final String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final RestaurantSpecialty[] VALUES = RestaurantSpecialty.values();

    /**
     * Gets the RestaurantSpecialty value by ordinal if it exists, or null otherwise.
     */
    public static RestaurantSpecialty fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}
