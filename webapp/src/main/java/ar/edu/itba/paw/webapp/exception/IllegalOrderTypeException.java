package ar.edu.itba.paw.webapp.exception;

public class IllegalOrderTypeException extends RuntimeException{
    public IllegalOrderTypeException(String orderTypeNotSupported) {
        super(orderTypeNotSupported);
    }
}
