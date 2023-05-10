package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

public class RepeatPasswordsMatchValidator implements ConstraintValidator<RepeatPasswordsMatch, Object> {

    private String passwordFieldName;
    private String repeatPasswordFieldName;

    @Override
    public void initialize(RepeatPasswordsMatch repeatPasswordsMatch) {
        passwordFieldName = repeatPasswordsMatch.passwordField();
        repeatPasswordFieldName = repeatPasswordsMatch.repeatPasswordField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Field passwordField = o.getClass().getDeclaredField(passwordFieldName);
            Field repeatPasswordField = o.getClass().getDeclaredField(repeatPasswordFieldName);
            passwordField.setAccessible(true);
            repeatPasswordField.setAccessible(true);
            return Objects.equals(passwordField.get(o), repeatPasswordField.get(o));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
