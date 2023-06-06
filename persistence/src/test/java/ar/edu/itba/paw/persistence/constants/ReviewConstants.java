package ar.edu.itba.paw.persistence.constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ReviewConstants {

    public static final Long[] REVIEW_IDS_0 = OrderConstants.ORDER_IDS_RESTAURANT_0;
    public static final Long[] REVIEW_IDS_1 = OrderConstants.ORDER_IDS_RESTAURANT_1;
    public static final Long[] REVIEW_IDS_2 = OrderConstants.ORDER_IDS_RESTAURANT_2;
    public static final LocalDateTime[] REVIEW_DATES = {
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(4),
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(6)
    };
    public static final String REVIEW_COMMENT = "comment";
    public static final String REVIEW_REPLY = "reply";

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

    private ReviewConstants() {
    }
}
