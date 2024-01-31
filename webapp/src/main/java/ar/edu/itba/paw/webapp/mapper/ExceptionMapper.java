package ar.edu.itba.paw.webapp.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Component
@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    private static final Response.Status DEFAULT_STATUS = Response.Status.INTERNAL_SERVER_ERROR;

    private final Map<Class<? extends Exception>, Response.Status> EXCEPTION_STATUS_MAP = new HashMap<>();

    @Autowired
    private MessageSource messageSource;

    public ExceptionMapper() {
        putMapEntries();
    }

    private void putMapEntries() {
        EXCEPTION_STATUS_MAP.put(IllegalArgumentException.class, Response.Status.BAD_REQUEST);
    }

    @Override
    public Response toResponse(Exception exception) {

        final Response.Status status = EXCEPTION_STATUS_MAP.getOrDefault(exception.getClass(), DEFAULT_STATUS);

        LOGGER.error("Exception: {} - Message: {} - Status code: {}", exception.getClass(), exception.getMessage(), status.getStatusCode());

        String message;
        try {
            message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            message = exception.getMessage();
        }

        return Response
                .status(status)
                .entity(message)
                .build();
    }
}
