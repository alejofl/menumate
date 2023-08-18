package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exception.CannotSetRoleOfRestaurantOwnerException;
import ar.edu.itba.paw.exception.ResourceNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CannotSetRoleOfRestaurantOwnerExceptionMapper implements ExceptionMapper<CannotSetRoleOfRestaurantOwnerException> {
    @Override
    public Response toResponse(CannotSetRoleOfRestaurantOwnerException exception) {
        return Response.status(400).entity("Cannot change the role of a restaurant's owner").build();
    }
}
