package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.BadRequestException;

public class InvalidOrderTypeException extends BadRequestException {
    public InvalidOrderTypeException() {
        super("exception.InvalidOrderTypeException");
    }
}
