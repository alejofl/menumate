package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.BadRequestException;

public class InvalidUserArgumentException extends BadRequestException {
    public InvalidUserArgumentException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
