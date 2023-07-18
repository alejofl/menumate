package ar.edu.itba.paw.exception;

public class ProductDeletedException extends ResourceDeletedException {
    public ProductDeletedException() {
        super();
    }

    public ProductDeletedException(String message) {
        super(message);
    }

    public ProductDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductDeletedException(Throwable cause) {
        super(cause);
    }
}
