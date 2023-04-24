package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.exception.ImageNotFoundException;
import ar.edu.itba.paw.webapp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({ResourceNotFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView resourceNotFound() {
        return new ModelAndView("errors/404");
    }

    @ExceptionHandler(ImageNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView imageNotFound() {
        return new ModelAndView("errors/404-image");
    }
}
