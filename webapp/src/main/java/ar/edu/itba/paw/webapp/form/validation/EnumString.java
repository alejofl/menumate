package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a string represents a value in a specified enum.
 * <br>Can be applied to: String.
 */
@Constraint(validatedBy = EnumStringValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumString {

    Class<? extends java.lang.Enum<?>> enumClass();

    String message() default "Not a valid option";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}