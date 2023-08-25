package ar.edu.itba.paw.exception;

public class InvalidUserArgumentException extends ResourceNotFoundException {
    public InvalidUserArgumentException() {
        super();
    }

    public InvalidUserArgumentException(String message) {
        super(message);
    }

    public InvalidUserArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserArgumentException(Throwable cause) {
        super(cause);
    }
}
