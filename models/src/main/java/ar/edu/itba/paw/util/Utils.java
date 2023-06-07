package ar.edu.itba.paw.util;

public final class Utils {
    private static final int MAX_PAGINATION_PAGE_SIZE = 100;

    private Utils() {
        // Not instantiable
    }

    public static void validatePaginationParams(int pageNumber, int pageSize) {
        if (pageNumber <= 0)
            throw new IllegalArgumentException("pageNumber must be greater than 0");

        if (pageSize <= 0 || pageSize > MAX_PAGINATION_PAGE_SIZE)
            throw new IllegalArgumentException("pageSize must be greater than 0 but not greater than 100");
    }

    public static <T> T coalesce(T a, T b) {
        return a != null ? a : b;
    }
}
