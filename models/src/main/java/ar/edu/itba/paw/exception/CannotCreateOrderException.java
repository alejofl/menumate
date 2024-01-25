package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.BadRequestException;

public class CannotCreateOrderException extends BadRequestException {
    public CannotCreateOrderException() {
        super("exception.CannotCreateOrderException");
    }
}
