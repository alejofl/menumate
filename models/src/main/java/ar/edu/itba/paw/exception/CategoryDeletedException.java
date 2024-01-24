package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceDeletedException;

public class CategoryDeletedException extends ResourceDeletedException {
    public CategoryDeletedException() {
        super("exception.CategoryDeletedException");
    }
}
