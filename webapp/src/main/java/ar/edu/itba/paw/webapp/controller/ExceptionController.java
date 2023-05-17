package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView resourceNotFound(HttpServletRequest httpServletRequest, Exception exception) {
        LOGGER.error("{} - Resource Not Found - {}", exception.getClass(), httpServletRequest.getRequestURI());
        return new ModelAndView("errors/404");
    }
}
