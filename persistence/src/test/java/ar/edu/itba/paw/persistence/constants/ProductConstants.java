package ar.edu.itba.paw.persistence.constants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ProductConstants {
    public static final long[] PRODUCT_FROM_CATEGORY_RESTAURANT_0 = {100L, 101L, 102L, 103L, 104L, 105L};
    public static final long[] PRODUCT_FROM_CATEGORY_RESTAURANT_1 = {200L, 201L, 202L, 203L, 204L, 205L};
    public static final long PRODUCT_DELETED_FROM_CATEGORY_RESTAURANT_0 = 300L;
    public static final long PRODUCT_DELETED_FROM_CATEGORY_RESTAURANT_1 = 400L;
    public static final String DEFAULT_PRODUCT_NAME = "product";
    public static final String DEFAULT_PRODUCT_DESCRIPTION = "description";
    public static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal("535.55");

    public static final List<List<Double>> VALUES = Arrays.asList(
            Arrays.asList(100.0, 200.0, 300.0, 400.0, 500.0, 600.0),
            Arrays.asList(700.0, 800.0, 900.0, 1000.0, 1100.0, 1200.0),
            Collections.singletonList(0.0),
            Collections.singletonList(100.0)
    );
    public static final List<Double> AVERAGE_LIST = VALUES.stream()
            .mapToDouble(sublist -> sublist.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0))
            .boxed()
            .collect(Collectors.toList());

    public static final long[] ORDER_ITEMS_FOR_ORDER_IDS = {1700L, 1701L, 1702L};
    public static final int[] LINE_NUMBER_FOR_ORDER_IDS = {1, 2, 3};
    public static final long[] PRODUCTS_FOR_ORDER_IDS = {100L, 101L, 102L};
    public static final String DEFAULT_ORDER_ITEM_COMMENT = "comment";
    public static final String DEFAULT_STRING = "new";
    public static final int DEFAULT_ORDER_ITEM_QUANTITY = 10;
    public static final long PROMOTION_ID = 100L;
    public static final long NO_PROMOTION_ID = 50000L;
    public static final long PROMOTION_SOURCE_ID = 501L;
    public static final long PROMOTION_DESTINATION_ID = 500L;
    public static final BigDecimal DEFAULT_PROMOTION_DISCOUNT = BigDecimal.valueOf(50);
    public static final LocalDateTime DEFAULT_PROMOTION_START_DATE = LocalDateTime.now();
    public static final LocalDateTime DEFAULT_PROMOTION_END_DATE = LocalDateTime.now().plusDays(4);

    private ProductConstants() {
        throw new AssertionError();
    }
}
