package ar.edu.itba.paw.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException() {
        super("exception.CategoryNotFoundException");
    }
}
