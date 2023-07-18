package ar.edu.itba.paw.exception;

/**
 * A base class for all resource-deleted type exceptions.
 */
public class ResourceDeletedException extends RuntimeException {
    public ResourceDeletedException() {
        super();
    }

    public ResourceDeletedException(String message) {
        super(message);
    }

    public ResourceDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceDeletedException(Throwable cause) {
        super(cause);
    }
}
