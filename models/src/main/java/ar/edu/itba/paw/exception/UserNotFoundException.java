package ar.edu.itba.paw.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("exception.UserNotFoundException");
    }
}
