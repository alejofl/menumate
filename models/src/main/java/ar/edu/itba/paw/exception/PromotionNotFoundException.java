package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class PromotionNotFoundException extends ResourceNotFoundException {
    public PromotionNotFoundException() {
        super("exception.PromotionNotFoundException");
    }
}
