package ar.edu.itba.paw.exception;

public class InvalidOrderTypeException extends BadRequestException {
    public InvalidOrderTypeException() {
        super("exception.InvalidOrderTypeException");
    }
}
