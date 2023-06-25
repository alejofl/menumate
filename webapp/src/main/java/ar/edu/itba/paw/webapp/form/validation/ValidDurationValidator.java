package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.PromotionType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class ValidDurationValidator implements ConstraintValidator<ValidDuration, Object> {

    private String typeFieldName;
    private String daysFieldName;
    private String hoursFieldName;
    private String minutesFieldName;

    @Override
    public void initialize(ValidDuration annotation) {
        typeFieldName = annotation.typeField();
        daysFieldName = annotation.daysField();
        hoursFieldName = annotation.hoursField();
        minutesFieldName = annotation.minutesField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Field daysField = o.getClass().getDeclaredField(daysFieldName);
            Field hoursField = o.getClass().getDeclaredField(hoursFieldName);
            Field minutesField = o.getClass().getDeclaredField(minutesFieldName);
            Field typeField = o.getClass().getDeclaredField(typeFieldName);
            daysField.setAccessible(true);
            hoursField.setAccessible(true);
            minutesField.setAccessible(true);
            typeField.setAccessible(true);

            int days = (int) daysField.get(o);
            int hours = (int) hoursField.get(o);
            int minutes = (int) minutesField.get(o);
            PromotionType type = PromotionType.fromOrdinal((int) typeField.get(o));
            return type == PromotionType.SCHEDULED || (type == PromotionType.INSTANT && (days > 0 || hours > 0 || minutes > 0));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
