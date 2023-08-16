package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BothFieldsNullValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BothFieldsNull {
    String message() default "End datetime must be after Start datetime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowBothNull();

    boolean allowBothNotNull();

    String field1Name();

    String field2Name();
}
