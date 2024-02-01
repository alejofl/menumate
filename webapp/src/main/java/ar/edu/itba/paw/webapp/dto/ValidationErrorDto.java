package ar.edu.itba.paw.webapp.dto;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationErrorDto {
    private String message;
    private String path;

    public ValidationErrorDto() {

    }

    public ValidationErrorDto(String message, String path) {
        this.message = message;
        this.path = path;
    }

    public static ValidationErrorDto fromConstraintViolation(final ConstraintViolation<?> violation) {
        ValidationErrorDto dto = new ValidationErrorDto();
        dto.message = violation.getMessage();
        dto.path = violation.getPropertyPath().toString();

        return dto;
    }

    public static List<ValidationErrorDto> fromConstraintViolationCollection(final Collection<ConstraintViolation<?>> violations) {
        return violations.stream().map(ValidationErrorDto::fromConstraintViolation).collect(Collectors.toList());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}