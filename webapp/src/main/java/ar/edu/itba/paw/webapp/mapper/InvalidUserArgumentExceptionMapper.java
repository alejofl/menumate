package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.webapp.dto.ValidationErrorDto;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidUserArgumentExceptionMapper implements ExceptionMapper<InvalidUserArgumentException> {
    @Override
    public Response toResponse(InvalidUserArgumentException exception) {
        final ValidationErrorDto dto = new ValidationErrorDto(exception.getMessage(), null);
        return Response.status(Response.Status.BAD_REQUEST).entity(dto).build();
    }
}
