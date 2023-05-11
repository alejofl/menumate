package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.authentication.DisabledException;

public class UserNotVerifiedException extends DisabledException {
    private final long userId;

    public UserNotVerifiedException(final String msg, final long userId) {
        super(msg);
        this.userId = userId;
    }

    public UserNotVerifiedException(final String msg, final Throwable t, final long userId) {
        super(msg, t);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
