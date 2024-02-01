package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EnumMessageCodeListValidator implements ConstraintValidator<EnumMessageCodeList, List<String>> {

    private List<String> valueList = null;

    @Override
    public void initialize(EnumMessageCodeList constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        Enum[] enumValArr = enumClass.getEnumConstants();
        try {
            Method getMessageCodeMethod = enumClass.getDeclaredMethod("getMessageCode");

            valueList = new ArrayList<>();
            for (Enum enumVal : enumValArr)
                valueList.add((String) getMessageCodeMethod.invoke(enumVal));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("@EnumMessageCodeList must be used with an enum that has a getMessageCode() method", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidSingle(String value, ConstraintValidatorContext context) {
        String trimmed = value == null ? null : value.trim();
        if (trimmed == null || trimmed.isEmpty())
            return false;

        for (String s : valueList)
            if (s.equalsIgnoreCase(trimmed))
                return true;
        return false;
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        for (String v : value) {
            if (!isValidSingle(v, context))
                return false;
        }

        return true;
    }
}