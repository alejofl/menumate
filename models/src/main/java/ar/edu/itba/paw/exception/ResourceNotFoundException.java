package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.CustomRuntimeException;
import ar.edu.itba.paw.exception.base.ExceptionUtils;

/**
 * A base class for all resource-not-found type exceptions. The idea of this base class is being able to identify
 * these errors and being able to handle them separately.
 */
public class ResourceNotFoundException extends CustomRuntimeException {
    public ResourceNotFoundException(String message) {
        super(ExceptionUtils.NOT_FOUND_STATUS_CODE, message);
    }
}
