package ar.edu.itba.paw.webapp.exception;

/**
 * A base class for all resource-not-found type exceptions. The idea of this base class is being able to identify
 * these errors and being able to handle them separately.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
