package ar.edu.itba.paw.webapp.exception;

public class IllegalOrderTypeException extends RuntimeException {
    public IllegalOrderTypeException() {
        super();
    }

    public IllegalOrderTypeException(String message) {
        super(message);
    }

    public IllegalOrderTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalOrderTypeException(Throwable cause) {
        super(cause);
    }
}
