package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class BothFieldsNullValidator implements ConstraintValidator<BothFieldsNull, Object> {
    private boolean allowBothNull;
    private boolean allowBothNotNull;
    private String field1Name;
    private String field2Name;

    @Override
    public void initialize(BothFieldsNull bothFieldsNull) {
        allowBothNull = bothFieldsNull.allowBothNull();
        allowBothNotNull = bothFieldsNull.allowBothNotNull();
        field1Name = bothFieldsNull.field1Name();
        field2Name = bothFieldsNull.field2Name();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Field field1 = o.getClass().getDeclaredField(field1Name);
            field1.setAccessible(true);
            Field field2 = o.getClass().getDeclaredField(field2Name);
            field2.setAccessible(true);

            Object o1 = field1.get(o);
            Object o2 = field2.get(o);

            if (!allowBothNull && o1 == null && o2 == null)
                return false;

            if (!allowBothNotNull && o1 != null && o2 != null)
                return false;

            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("BothFieldsNull exception during validation", e);
        }
    }
}
