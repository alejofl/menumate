package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.authentication.DisabledException;

public class UserNotVerifiedException extends DisabledException {

    private final String email;

    public UserNotVerifiedException(String msg, String email) {
        super(msg);
        this.email = email;
    }

    public UserNotVerifiedException(String msg, Throwable t, String email) {
        super(msg, t);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
