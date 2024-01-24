package ar.edu.itba.paw.exception;

public class RestaurantDetailsNotFoundException extends ResourceNotFoundException {
    public RestaurantDetailsNotFoundException() {
        super("exception.RestaurantDetailsNotFoundException");
    }
}
