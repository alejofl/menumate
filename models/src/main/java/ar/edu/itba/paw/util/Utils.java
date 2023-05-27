package ar.edu.itba.paw.util;

public class Utils {
    private Utils() {
        // Not instantiable
    }

    public static void validatePaginationParams(int pageNumber, int pageSize) {
        if (pageNumber <= 0)
            throw new IllegalArgumentException("pageNumber must be greater than 0");

        if (pageSize <= 0 || pageSize > 100)
            throw new IllegalArgumentException("pageSize must be greater than 0 but not greater than 100");
    }
}
