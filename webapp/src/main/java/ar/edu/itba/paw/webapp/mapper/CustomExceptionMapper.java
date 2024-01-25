package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exception.base.CustomException;
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

@Provider
@Component
public class CustomExceptionMapper implements ExceptionMapper<CustomException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(CustomException exception) {
        LOGGER.error("Exception: {} - Status code: {} - Message: {}",
                exception.getClass().getName(),
                exception.getStatusCode(),
                messageSource.getMessage(exception.getExceptionMessage(), null, Locale.ENGLISH));

        return Response.status(exception.getStatusCode())
                .entity(messageSource.getMessage(exception.getExceptionMessage(), null, LocaleContextHolder.getLocale()))
                .build();
    }
}