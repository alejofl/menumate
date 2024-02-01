package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceDeletedException;

public class ProductDeletedException extends ResourceDeletedException {
    public ProductDeletedException() {
        super("exception.ProductDeletedException");
    }
}
