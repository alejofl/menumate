package ar.edu.itba.paw.persistence.constants;

public final class CategoryConstants {

    public static final Long[] CATEGORY_IDS_FOR_RESTAURANT_0 = {901L, 50L, 25L};
    public static final Long[] CATEGORY_IDS_FOR_RESTAURANT_1 = {320L, 750L};
    public static final Long[] CATEGORY_IDS_FOR_RESTAURANT_2 = {};
    public static final Integer[] CATEGORY_ORDER_FOR_RESTAURANT_0 = {1, 2, 3};
    public static final Integer[] CATEGORY_ORDER_FOR_RESTAURANT_1 = {1, 2};
    public static final Integer[] CATEGORY_ORDER_FOR_RESTAURANT_2 = {};
    public static final int TOTAL_COUNT = CATEGORY_IDS_FOR_RESTAURANT_0.length + CATEGORY_IDS_FOR_RESTAURANT_1.length + CATEGORY_IDS_FOR_RESTAURANT_2.length;
    public static final Long DELETED_CATEGORY_ID = 25L;
    public static final String CATEGORY_NAME = "category";
    public static final String CATEGORY_DESCRIPTION = "description";

    private CategoryConstants() {
    }

}
