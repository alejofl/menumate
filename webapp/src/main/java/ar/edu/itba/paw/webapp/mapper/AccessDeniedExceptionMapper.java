package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedExceptionMapper.class);

    @Override
    public Response toResponse(AccessDeniedException exception) {
        final Response.Status status = ControllerUtils.getCurrentUserIdOrNull() == null ? Response.Status.UNAUTHORIZED : Response.Status.FORBIDDEN;

        Response.ResponseBuilder response = Response.status(status).entity(exception.getMessage());
        if (status == Response.Status.UNAUTHORIZED) {
            response = response.header("WWW-Authenticate", "Basic realm=\"MenuMate\", Bearer realm=\"MenuMate\"");
        }

        LOGGER.error("AccessDeniedException: {} - Message: {} - Status code: {}",
                exception.getClass(),
                exception.getMessage(),
                status.getStatusCode());

        return response.build();
    }
}