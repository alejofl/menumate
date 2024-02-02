package ar.edu.itba.paw.webapp.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(AuthenticationException exception) {
        final Response.Status status = Response.Status.UNAUTHORIZED;

        LOGGER.error("AuthenticationException: {} - Message: {} - Status code: {}",
                exception.getClass(),
                exception.getMessage(),
                status.getStatusCode());

        String message;
        try {
            message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            message = exception.getMessage();
        }

        return Response
                .status(status)
                .header("WWW-Authenticate", "Basic realm=\"MenuMate\", Bearer realm=\"MenuMate\"")
                .entity(message)
                .build();
    }
}
