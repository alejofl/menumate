package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.CustomRuntimeException;
import ar.edu.itba.paw.exception.base.ExceptionUtils;

public class InvalidUserArgumentException extends CustomRuntimeException {
    public InvalidUserArgumentException(String message) {
        super(ExceptionUtils.BAD_REQUEST_STATUS_CODE, message);
    }
}
