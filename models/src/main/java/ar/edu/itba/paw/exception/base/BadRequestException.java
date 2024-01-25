package ar.edu.itba.paw.exception.base;


/**
 * A base class for all bad requests type exceptions.
 */
public class BadRequestException extends CustomRuntimeException {

    public BadRequestException(String exceptionMessage) {
        super(ExceptionUtils.BAD_REQUEST_STATUS_CODE, exceptionMessage);
    }
}
