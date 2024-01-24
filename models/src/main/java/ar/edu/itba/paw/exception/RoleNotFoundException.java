package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException() {
        super("exception.RoleNotFoundException");
    }
}
