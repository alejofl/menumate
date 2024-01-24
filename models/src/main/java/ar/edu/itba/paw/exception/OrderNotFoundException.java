package ar.edu.itba.paw.exception;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException() {
        super("exception.OrderNotFoundException");
    }

}
