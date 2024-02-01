package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class UserRoleNotFoundException extends ResourceNotFoundException {
    public UserRoleNotFoundException() {
        super("exception.UserRoleNotFoundException");
    }
}
