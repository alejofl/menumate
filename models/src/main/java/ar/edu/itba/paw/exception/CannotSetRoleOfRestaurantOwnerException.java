package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.CustomRuntimeException;
import ar.edu.itba.paw.exception.base.ExceptionUtils;

public class CannotSetRoleOfRestaurantOwnerException extends CustomRuntimeException {
    public CannotSetRoleOfRestaurantOwnerException() {
        super(ExceptionUtils.BAD_REQUEST_STATUS_CODE, "exception.CannotSetRoleOfRestaurantOwnerException");
    }
}
