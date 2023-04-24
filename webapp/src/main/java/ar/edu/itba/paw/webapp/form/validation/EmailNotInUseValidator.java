package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailNotInUseValidator implements ConstraintValidator<EmailNotInUse, String> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(EmailNotInUse emailNotInUse) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.length() < 3)
            return false;

        return !userService.isUserEmailRegistered(s);
    }
}
