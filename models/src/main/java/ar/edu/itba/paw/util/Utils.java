package ar.edu.itba.paw.util;

public final class Utils {
    private static final int MAX_PAGINATION_PAGE_SIZE = 100;

    private Utils() {
        throw new AssertionError();
    }

    public static void validatePaginationParams(int pageNumber, int pageSize) {
        if (pageNumber <= 0)
            throw new IllegalArgumentException("exception.IllegalArgumentException.validatePaginationParams.pageNumber");

        if (pageSize <= 0 || pageSize > MAX_PAGINATION_PAGE_SIZE)
            throw new IllegalArgumentException("exception.IllegalArgumentException.validatePaginationParams.pageSize");
    }

    public static <T> T coalesce(T a, T b) {
        return a != null ? a : b;
    }
}
