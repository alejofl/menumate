package ar.edu.itba.paw.exception;

public class TokenNotFoundException extends ResourceNotFoundException {
    public TokenNotFoundException() {
        super();
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenNotFoundException(Throwable cause) {
        super(cause);
    }
}
