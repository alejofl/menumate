package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class ReportNotFoundException extends ResourceNotFoundException {
    public ReportNotFoundException() {
        super("exception.ReportNotFoundException");
    }
}
