package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UserExistsValidatorConstraint implements ConstraintValidator<UserExists, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UserExists userExists) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return userService.isUserEmailRegistered(email);
    }
}
