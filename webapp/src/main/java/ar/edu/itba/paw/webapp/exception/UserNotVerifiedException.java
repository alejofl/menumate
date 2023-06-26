package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.model.User;
import org.springframework.security.authentication.DisabledException;

public class UserNotVerifiedException extends DisabledException {
    private final User user;

    public UserNotVerifiedException(final String msg, final User user) {
        super(msg);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
