package ar.edu.itba.paw.exception.base;

/**
 * A base class for all resource-deleted type exceptions.
 */
public class ResourceDeletedException extends CustomRuntimeException {
    public ResourceDeletedException(String exceptionMessage) {
        super(ExceptionUtils.GONE_STATUS_CODE, exceptionMessage);
    }
}
