package ar.edu.itba.paw.exception.base;

/**
 * https://medium.com/@satyendra.jaiswal/demystifying-exception-mappers-in-jax-rs-handling-errors-with-grace-b1a038642829
 * https://www.baeldung.com/java-exception-handling-jersey
 * https://www.mastertheboss.com/jboss-frameworks/resteasy/how-to-handle-exceptions-in-jax-rs-applications/
 */
public class CustomRuntimeException extends RuntimeException implements CustomBaseException {

    private final int statusCode;
    private final String exceptionMessage;

    public CustomRuntimeException(int statusCode, String exceptionMessage) {
        super();
        this.statusCode = statusCode;
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
