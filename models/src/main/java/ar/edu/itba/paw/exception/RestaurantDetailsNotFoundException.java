package ar.edu.itba.paw.exception;

public class RestaurantDetailsNotFoundException extends ResourceNotFoundException {
    public RestaurantDetailsNotFoundException() {
        super();
    }

    public RestaurantDetailsNotFoundException(String message) {
        super(message);
    }

    public RestaurantDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantDetailsNotFoundException(Throwable cause) {
        super(cause);
    }
}
