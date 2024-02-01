package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumMessageCodeValidator implements ConstraintValidator<EnumMessageCode, String> {

    private List<String> valueList = null;
    private String[] excludeValues;

    @Override
    public void initialize(EnumMessageCode constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        Enum[] enumValArr = enumClass.getEnumConstants();
        try {
            Method getMessageCodeMethod = enumClass.getDeclaredMethod("getMessageCode");

            valueList = new ArrayList<>();
            for (Enum enumVal : enumValArr)
                valueList.add((String) getMessageCodeMethod.invoke(enumVal));

            String excludeValuesStr = constraintAnnotation.excludeValues();
            excludeValues = excludeValuesStr == null ? new String[0] : excludeValuesStr.split("\\|");
            for (int i = 0; i < excludeValues.length; i++)
                excludeValues[i] = excludeValues[i].trim();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("@EnumMessageCode must be used with an enum that has a getMessageCode() method", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String trimmed = value == null ? null : value.trim();
        if (trimmed == null || trimmed.isEmpty())
            return true;

        if (Arrays.stream(excludeValues).anyMatch(v -> v.equalsIgnoreCase(trimmed)))
            return false;

        for (String s : valueList)
            if (s.equalsIgnoreCase(trimmed))
                return true;
        return false;
    }
}