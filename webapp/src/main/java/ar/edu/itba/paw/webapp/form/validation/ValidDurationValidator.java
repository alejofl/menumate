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

            Integer days = (Integer) daysField.get(o);
            Integer hours = (Integer) hoursField.get(o);
            Integer minutes = (Integer) minutesField.get(o);

            PromotionType type = PromotionType.fromCode((String) typeField.get(o));
            return type == PromotionType.SCHEDULED || (type == PromotionType.INSTANT && ((days == null || days > 0) || (hours == null || hours > 0) || (minutes == null || minutes > 0)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
