package ar.edu.itba.paw.exception;

public class InvalidOrderTypeException extends RuntimeException {
    public InvalidOrderTypeException() {
        super();
    }

    public InvalidOrderTypeException(String message) {
        super(message);
    }

    public InvalidOrderTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOrderTypeException(Throwable cause) {
        super(cause);
    }
}
