package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.controller.ControllerUtils;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {
    @Override
    public Response toResponse(AccessDeniedException exception) {
        Response.Status status = ControllerUtils.getCurrentUserIdOrNull() == null ? Response.Status.UNAUTHORIZED : Response.Status.FORBIDDEN;
        return Response.status(status).build();
    }
}
