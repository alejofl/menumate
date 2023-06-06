package ar.edu.itba.paw.exception;

public class PromotionNotFoundException extends ResourceNotFoundException {
    public PromotionNotFoundException() {
        super();
    }

    public PromotionNotFoundException(String message) {
        super(message);
    }

    public PromotionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PromotionNotFoundException(Throwable cause) {
        super(cause);
    }
}
