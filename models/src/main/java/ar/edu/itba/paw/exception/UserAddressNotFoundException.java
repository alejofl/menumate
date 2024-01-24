package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class UserAddressNotFoundException extends ResourceNotFoundException {
    public UserAddressNotFoundException() {
        super("exception.UserAddressNotFoundException");
    }
}
