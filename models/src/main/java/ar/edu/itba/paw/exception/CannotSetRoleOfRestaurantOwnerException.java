package ar.edu.itba.paw.exception;

public class CannotSetRoleOfRestaurantOwnerException extends RuntimeException {
    public CannotSetRoleOfRestaurantOwnerException() {
        super();
    }

    public CannotSetRoleOfRestaurantOwnerException(String message) {
        super(message);
    }

    public CannotSetRoleOfRestaurantOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotSetRoleOfRestaurantOwnerException(Throwable cause) {
        super(cause);
    }
}
