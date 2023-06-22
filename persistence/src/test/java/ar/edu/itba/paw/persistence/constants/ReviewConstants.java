package ar.edu.itba.paw.persistence.constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ReviewConstants {

    public static final LocalDateTime[] REVIEW_DATES = {
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(4),
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(6)
    };

    public static final LocalDateTime DEFAULT_SINCE_WHEN = LocalDateTime.now();
    public static final String DEFAULT_REVIEW_COMMENT = "comment";
    public static final String DEFAULT_REVIEW_REPLY = "reply";
    public static final int DEFAULT_REVIEW_RATING = 5;

    public static final List<List<Integer>> VALUES = Arrays.asList(
            Arrays.asList(1, 1, 3, 5, 5, 5),
            Arrays.asList(2, 2, 2, 2, 2, 2),
            Arrays.asList(3, 3, 3, 3, 3, 3),
            Arrays.asList(0, 0, 0, 0, 0, 0)
    );
    public static final List<Double> AVERAGE_LIST = VALUES.stream()
            .mapToDouble(sublist -> sublist.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0))
            .boxed()
            .collect(Collectors.toList());

    private ReviewConstants() {
    }
}
