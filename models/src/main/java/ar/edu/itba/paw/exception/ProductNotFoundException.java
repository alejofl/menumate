package ar.edu.itba.paw.exception;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException() {
        super("exception.ProductNotFoundException");
    }
}
