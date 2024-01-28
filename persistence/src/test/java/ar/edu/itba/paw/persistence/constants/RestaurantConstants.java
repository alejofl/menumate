package ar.edu.itba.paw.persistence.constants;

import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RestaurantConstants {

    public static final long[] RESTAURANT_IDS = {506L, 600L, 1200L, 1300L};
    public static final long RESTAURANT_ID_WITH_NO_ORDERS = 1300L;
    public static final long RESTAURANT_ID_WITH_NO_REPORTS = 600L;
    public static final long RESTAURANT_ID_NON_EXISTENT = 10000L;
    public static final String[] RESTAURANT_NAMES = {"C", "A", "B", "D"};
    public static final LocalDateTime[] RESTAURANT_CREATION_DATES = {LocalDateTime.now(), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(3)};
    public static final String RESTAURANT_EMAIL = "restaurant@localhost";
    public static final int MAX_TABLES = 10;
    public static final List<RestaurantSpecialty> RESTAURANTS_SPECIALITY = Arrays.asList(RestaurantSpecialty.fromOrdinal(1), RestaurantSpecialty.fromOrdinal(2), RestaurantSpecialty.fromOrdinal(3), RestaurantSpecialty.fromOrdinal(4));
    public static final String RESTAURANT_ADDRESS = "somewhere";
    public static final String RESTAURANT_DESCRIPTION = "description";
    public static final LocalDateTime RESTAURANT_CREATION_DATE = LocalDateTime.now();
    public static final boolean RESTAURANT_IS_ACTIVE = true;
    public static final boolean RESTAURANT_DELETED = false;
    public static final List<List<RestaurantTags>> RESTAURANTS_TAGS = Arrays.asList(
            Arrays.asList(RestaurantTags.fromOrdinal(1), RestaurantTags.fromOrdinal(2), RestaurantTags.fromOrdinal(3)),
            Arrays.asList(RestaurantTags.fromOrdinal(1), RestaurantTags.fromOrdinal(2)),
            Collections.singletonList(RestaurantTags.fromOrdinal(1)),
            Collections.emptyList()
    );

    private RestaurantConstants() {
        throw new AssertionError();
    }
}
