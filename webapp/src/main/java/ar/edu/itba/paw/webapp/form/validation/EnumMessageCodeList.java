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
@Constraint(validatedBy = EnumMessageCodeListValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumMessageCodeList {
    String message() default "Not a valid option";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}