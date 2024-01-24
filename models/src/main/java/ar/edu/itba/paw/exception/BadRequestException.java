package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.CustomRuntimeException;
import ar.edu.itba.paw.exception.base.ExceptionUtils;

/**
 * A base class for all bad requests type exceptions.
 */
public class BadRequestException extends CustomRuntimeException {

    public BadRequestException(String message) {
        super(ExceptionUtils.BAD_REQUEST_STATUS_CODE, message);
    }
}
