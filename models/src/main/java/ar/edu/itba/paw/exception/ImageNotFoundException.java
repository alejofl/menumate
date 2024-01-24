package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class ImageNotFoundException extends ResourceNotFoundException {
    public ImageNotFoundException() {
        super("exception.ImageNotFoundException");
    }
}
