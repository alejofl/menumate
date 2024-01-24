package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceDeletedException;

public class RestaurantDeletedException extends ResourceDeletedException {
    public RestaurantDeletedException() {
        super("exception.RestaurantDeletedException");
    }
}
