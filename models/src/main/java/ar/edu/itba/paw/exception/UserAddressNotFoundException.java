package ar.edu.itba.paw.exception;

public class UserAddressNotFoundException extends ResourceNotFoundException {
    public UserAddressNotFoundException() {
        super();
    }

    public UserAddressNotFoundException(String message) {
        super(message);
    }

    public UserAddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAddressNotFoundException(Throwable cause) {
        super(cause);
    }
}
