package ar.edu.itba.paw.exception;

public class TokenGenerationException extends ResourceNotFoundException {
    public TokenGenerationException() {
        super();
    }

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenGenerationException(Throwable cause) {
        super(cause);
    }
}
