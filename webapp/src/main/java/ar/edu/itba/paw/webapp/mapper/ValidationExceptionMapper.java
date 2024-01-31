package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.ValidationErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        final Response.Status status = Response.Status.BAD_REQUEST;
        LOGGER.error("ConstraintViolationException: {} - Message: {} - Status code: {}",
                exception.getClass().getName(),
                exception.getConstraintViolations(),
                status.getStatusCode());

        final List<ValidationErrorDto> validationErrors = ValidationErrorDto.fromConstraintViolationCollection(exception.getConstraintViolations());
        return Response.status(status).entity(new GenericEntity<List<ValidationErrorDto>>(validationErrors) {}).build();
    }
}
