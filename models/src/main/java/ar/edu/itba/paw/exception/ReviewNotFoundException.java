package ar.edu.itba.paw.exception;

public class ReviewNotFoundException extends ResourceNotFoundException {
    public ReviewNotFoundException() {
        super("exception.ReviewNotFoundException");
    }
}
