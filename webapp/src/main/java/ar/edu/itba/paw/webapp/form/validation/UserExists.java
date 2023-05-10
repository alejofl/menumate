package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UserExistsValidatorConstraint.class)
@Documented
public @interface UserExists {

    String message() default "The email is not associated to any user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
