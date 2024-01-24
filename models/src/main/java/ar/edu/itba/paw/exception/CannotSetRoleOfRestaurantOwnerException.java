package ar.edu.itba.paw.exception;

public class CannotSetRoleOfRestaurantOwnerException extends BadRequestException {
    public CannotSetRoleOfRestaurantOwnerException() {
        super("exception.CannotSetRoleOfRestaurantOwnerException");
    }
}
