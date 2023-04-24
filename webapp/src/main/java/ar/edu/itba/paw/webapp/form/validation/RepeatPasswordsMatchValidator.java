package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.webapp.form.RegisterForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class RepeatPasswordsMatchValidator implements ConstraintValidator<RepeatPasswordsMatch, RegisterForm> {
    @Override
    public void initialize(RepeatPasswordsMatch repeatPasswordsMatch) {

    }

    @Override
    public boolean isValid(RegisterForm registerForm, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(registerForm.getPassword(), registerForm.getRepeatPassword());
    }
}
