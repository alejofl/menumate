package ar.edu.itba.paw.persistence.constants;

import ar.edu.itba.paw.model.OrderType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderConstants {
    public static final Long[] ORDER_IDS_RESTAURANT_0 = {1700L, 1701L, 1702L, 1703L, 1704L, 1705L};
    public static final Long[] ORDER_IDS_RESTAURANT_1 = {2000L, 2001L, 2002L, 2003L, 2004L, 2005L};
    public static final Long[] ORDER_IDS_RESTAURANT_2 = {3000L, 3001L, 3002L, 3003L, 3004L, 3005L};
    public static final OrderType DEFAULT_ORDER_TYPE = OrderType.TAKEAWAY;
    public static final int TOTAL_ORDER_COUNT = ORDER_IDS_RESTAURANT_0.length + ORDER_IDS_RESTAURANT_1.length + ORDER_IDS_RESTAURANT_2.length;
    public static final int DEFAULT_ORDER_TABLE = 10;
    public static final String DEFAULT_ORDER_ADDRESS = "address";
    public static final LocalDateTime[] ORDER_DATES = {
            LocalDateTime.now(),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(3),
            LocalDateTime.now().minusDays(4),
            LocalDateTime.now().minusDays(5)
    };

    private OrderConstants() {
    }
}
