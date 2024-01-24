package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException() {
        super("exception.ProductNotFoundException");
    }
}
