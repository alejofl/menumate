package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.authentication.DisabledException;

public class UserNotVerifiedException extends DisabledException {

    private final int userId;

    public UserNotVerifiedException(final String msg, final int userId) {
        super(msg);
        this.userId = userId;
    }

    public UserNotVerifiedException(final String msg, final Throwable t, final int userId) {
        super(msg, t);
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
