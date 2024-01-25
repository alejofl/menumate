package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.model.User;
import org.springframework.security.authentication.DisabledException;

public class UserNotVerifiedException extends DisabledException {
    private final User user;
    private static final String MESSAGGE = "exception.UserNotVerifiedException";

    public UserNotVerifiedException(User user) {
        super(MESSAGGE);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
