package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exception.ResourceDeletedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceDeletedExceptionMapper implements ExceptionMapper<ResourceDeletedException> {
    @Override
    public Response toResponse(ResourceDeletedException exception) {
        return Response.status(Response.Status.GONE).build();
    }
}
