package ar.edu.itba.paw.exception;

public class RestaurantDeletedException extends ResourceDeletedException {
    public RestaurantDeletedException() {
        super();
    }

    public RestaurantDeletedException(String message) {
        super(message);
    }

    public RestaurantDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantDeletedException(Throwable cause) {
        super(cause);
    }
}
