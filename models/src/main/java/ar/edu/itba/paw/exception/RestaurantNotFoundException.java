package ar.edu.itba.paw.exception;

public class RestaurantNotFoundException extends ResourceNotFoundException {
    public RestaurantNotFoundException() {
        super("exception.RestaurantNotFoundException");
    }
}
