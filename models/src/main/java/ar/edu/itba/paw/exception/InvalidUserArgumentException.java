package ar.edu.itba.paw.exception;

public class InvalidUserArgumentException extends BadRequestException {
    public InvalidUserArgumentException(String message) {
        super(message);
    }
}
