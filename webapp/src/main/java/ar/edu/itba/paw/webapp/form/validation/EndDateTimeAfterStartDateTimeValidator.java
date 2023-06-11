package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class EndDateTimeAfterStartDateTimeValidator implements ConstraintValidator<EndDateTimeAfterStartDateTime, Object> {

    private String startDateTimeFieldName;
    private String endDateTimeFieldName;

    @Override
    public void initialize(EndDateTimeAfterStartDateTime annotation) {
        startDateTimeFieldName = annotation.startDateTimeField();
        endDateTimeFieldName = annotation.endDateTimeField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Field startDateTimeField = o.getClass().getDeclaredField(startDateTimeFieldName);
            Field endDateTimeField = o.getClass().getDeclaredField(endDateTimeFieldName);
            startDateTimeField.setAccessible(true);
            endDateTimeField.setAccessible(true);

            LocalDateTime startDateTime = (LocalDateTime) startDateTimeField.get(o);
            LocalDateTime endDateTime = (LocalDateTime) endDateTimeField.get(o);
            return startDateTime != null && endDateTime != null && endDateTime.isAfter(startDateTime);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
