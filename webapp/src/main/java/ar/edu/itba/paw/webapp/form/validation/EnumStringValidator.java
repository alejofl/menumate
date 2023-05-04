package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class EnumStringValidator implements ConstraintValidator<EnumString, String> {

    List<String> valueList = null;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (String s : valueList)
            if (s.equalsIgnoreCase(value))
                return true;
        return false;
    }

    @Override
    public void initialize(EnumString constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        Enum[] enumValArr = enumClass.getEnumConstants();

        valueList = new ArrayList<>();
        for (Enum enumVal : enumValArr)
            valueList.add(enumVal.toString());
    }
}