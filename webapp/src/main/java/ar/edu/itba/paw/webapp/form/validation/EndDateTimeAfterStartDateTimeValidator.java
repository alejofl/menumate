package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.PromotionType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class EndDateTimeAfterStartDateTimeValidator implements ConstraintValidator<EndDateTimeAfterStartDateTime, Object> {

    private String startDateTimeFieldName;
    private String endDateTimeFieldName;
    private String typeFieldName;

    @Override
    public void initialize(EndDateTimeAfterStartDateTime annotation) {
        startDateTimeFieldName = annotation.startDateTimeField();
        endDateTimeFieldName = annotation.endDateTimeField();
        typeFieldName = annotation.typeField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Field startDateTimeField = o.getClass().getDeclaredField(startDateTimeFieldName);
            Field endDateTimeField = o.getClass().getDeclaredField(endDateTimeFieldName);
            Field typeField = o.getClass().getDeclaredField(typeFieldName);
            startDateTimeField.setAccessible(true);
            endDateTimeField.setAccessible(true);
            typeField.setAccessible(true);

            LocalDateTime startDateTime = (LocalDateTime) startDateTimeField.get(o);
            LocalDateTime endDateTime = (LocalDateTime) endDateTimeField.get(o);
            PromotionType type = PromotionType.fromOrdinal((int) typeField.get(o));
            return type == PromotionType.INSTANT || (type == PromotionType.SCHEDULED && startDateTime != null && endDateTime != null && endDateTime.isAfter(startDateTime));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
