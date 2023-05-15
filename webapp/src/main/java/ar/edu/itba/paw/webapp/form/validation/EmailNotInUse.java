package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that an email is not already in use by a user. Returns true if there is a registered user, with said email
 * and a non-null password, in the database.
 * <br>Can be applied to: String.
 */
@Constraint(validatedBy = EmailNotInUseValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailNotInUse {
    String message() default "Email already in use!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
