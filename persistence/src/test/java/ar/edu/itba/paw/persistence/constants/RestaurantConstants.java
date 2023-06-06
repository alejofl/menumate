package ar.edu.itba.paw.persistence.constants;

import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantConstants {

    public static final Long[] RESTAURANT_IDS = {506L, 600L, 1200L};
    public static final String[] RESTAURANT_NAMES ={"C", "A", "B"};
    public static final LocalDateTime[] RESTAURANT_CREATION_DATES = {LocalDateTime.now(), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2)};
    public static final String RESTAURANT_EMAIL = "restaurant@localhost";
    public static final int MAX_TABLES = 10;
    public static final List<RestaurantSpecialty> RESTAURANTS_SPECIALITY = Arrays.asList(RestaurantSpecialty.fromOrdinal(1), RestaurantSpecialty.fromOrdinal(2), RestaurantSpecialty.fromOrdinal(3));
    public static final String RESTAURANT_ADDRESS = "somewhere";
    public static final String RESTAURANT_DESCRIPTION = "description";
    public static final LocalDateTime RESTAURANT_CREATION_DATE = LocalDateTime.now();
    public static final boolean RESTAURANT_IS_ACTIVE = true;
    public static final boolean RESTAURANT_DELETED = false;
    public static List<List<Double>> VALUES = Arrays.asList(
            Arrays.asList(1.0, 1.0, 3.0, 5.0, 5.0, 5.0),
            Arrays.asList(2.0, 2.0, 2.0, 2.0, 2.0, 2.0),
            Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0)
    );
    public static final List<Double> AVERAGE_LIST = VALUES.stream()
            .mapToDouble(sublist -> sublist.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0))
            .boxed()
            .collect(Collectors.toList());
    public static final List<List<RestaurantTags>> RESTAURANTS_TAGS = Arrays.asList(
            Arrays.asList(RestaurantTags.fromOrdinal(1), RestaurantTags.fromOrdinal(2), RestaurantTags.fromOrdinal(3)),
            Arrays.asList(RestaurantTags.fromOrdinal(1), RestaurantTags.fromOrdinal(2)),
            Arrays.asList(RestaurantTags.fromOrdinal(1))
    );
}
