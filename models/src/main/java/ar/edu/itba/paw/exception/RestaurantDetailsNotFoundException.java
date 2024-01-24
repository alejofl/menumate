package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class RestaurantDetailsNotFoundException extends ResourceNotFoundException {
    public RestaurantDetailsNotFoundException() {
        super("exception.RestaurantDetailsNotFoundException");
    }
}
