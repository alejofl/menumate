package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exception.base.CustomRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Component
@Provider
public class CustomRuntimeExceptionMapper implements ExceptionMapper<CustomRuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRuntimeException.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(CustomRuntimeException exception) {
        LOGGER.error("Runtime Exception: {} - Status code: {} - Message: {}",
                exception.getClass().getName(),
                exception.getStatusCode(),
                messageSource.getMessage(exception.getExceptionMessage(), null, Locale.ENGLISH));

        return Response
                .status(exception.getStatusCode())
                .entity(messageSource.getMessage(exception.getExceptionMessage(), null, LocaleContextHolder.getLocale()))
                .build();
    }
}