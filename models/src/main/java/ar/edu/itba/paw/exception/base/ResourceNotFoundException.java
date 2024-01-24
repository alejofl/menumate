package ar.edu.itba.paw.exception.base;

/**
 * A base class for all resource-not-found type exceptions. The idea of this base class is being able to identify
 * these errors and being able to handle them separately.
 */
public class ResourceNotFoundException extends CustomRuntimeException {
    public ResourceNotFoundException(String exceptionMessage) {
        super(ExceptionUtils.NOT_FOUND_STATUS_CODE, exceptionMessage);
    }
}
