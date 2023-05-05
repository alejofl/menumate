package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.authentication.DisabledException;

public class UserNotVerifiedException extends DisabledException {

    public UserNotVerifiedException(String msg) {
        super(msg);
    }

    public UserNotVerifiedException(String msg, Throwable t) {
        super(msg, t);
    }
}
