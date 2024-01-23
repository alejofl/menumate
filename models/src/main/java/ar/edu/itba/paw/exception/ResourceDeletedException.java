package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.CustomRuntimeException;
import ar.edu.itba.paw.exception.base.ExceptionUtils;

/**
 * A base class for all resource-deleted type exceptions.
 */
public class ResourceDeletedException extends CustomRuntimeException {
    public ResourceDeletedException(String exceptionMessage) {
        super(ExceptionUtils.GONE_STATUS_CODE, exceptionMessage);
    }
}
