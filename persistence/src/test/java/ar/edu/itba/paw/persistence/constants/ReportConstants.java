package ar.edu.itba.paw.persistence.constants;

public final class ReportConstants {
    public static final long[] REPORT_IDS = {7711, 7064, 6987, 4040};

    public static final long NONEXISTING_REPORT_ID = 4041;

    public static final long[] UNHANDLED_REPORT_IDS = {7711, 4040};

    public static final long[] HANDLED_REPORT_IDS = {7064, 6987};

    public static final long[] RESTAURANT_IDS_WITH_REPORTS = {506, 1200, 1300};

    public static final long[] REPORT_IDS_FROM_FIRST_RESTAURANT = {7711};

    private ReportConstants() {
        throw new AssertionError();
    }
}
