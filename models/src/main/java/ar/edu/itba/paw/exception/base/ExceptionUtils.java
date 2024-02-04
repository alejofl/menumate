package ar.edu.itba.paw.exception.base;

public final class ExceptionUtils {

    public static final int BAD_REQUEST_STATUS_CODE = 400;
    public static final int NOT_FOUND_STATUS_CODE = 404;
    public static final int GONE_STATUS_CODE = 410;

    private ExceptionUtils() {
        throw new AssertionError();
    }
}
