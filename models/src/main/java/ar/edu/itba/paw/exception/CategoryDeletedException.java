package ar.edu.itba.paw.exception;

public class CategoryDeletedException extends ResourceDeletedException {
    public CategoryDeletedException() {
        super();
    }

    public CategoryDeletedException(String message) {
        super(message);
    }

    public CategoryDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryDeletedException(Throwable cause) {
        super(cause);
    }
}
