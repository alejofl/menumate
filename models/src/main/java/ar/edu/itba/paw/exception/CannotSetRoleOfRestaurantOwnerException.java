package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.BadRequestException;

public class CannotSetRoleOfRestaurantOwnerException extends BadRequestException {
    public CannotSetRoleOfRestaurantOwnerException() {
        super("exception.CannotSetRoleOfRestaurantOwnerException");
    }
}
