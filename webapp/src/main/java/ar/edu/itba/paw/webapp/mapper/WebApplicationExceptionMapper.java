package ar.edu.itba.paw.webapp.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/content/en/part1/chapter7/exception_handling.html
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        LOGGER.error("WebApplicationException: {} - Message: {} - Status code: {}",
                exception.getClass().getName(),
                exception.getMessage(),
                exception.getResponse().getStatus());

        return Response
                .status(exception.getResponse().getStatus())
                .entity(exception.getMessage())
                .build();
    }
}

