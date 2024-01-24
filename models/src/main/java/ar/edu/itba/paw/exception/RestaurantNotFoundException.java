package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class RestaurantNotFoundException extends ResourceNotFoundException {
    public RestaurantNotFoundException() {
        super("exception.RestaurantNotFoundException");
    }
}
