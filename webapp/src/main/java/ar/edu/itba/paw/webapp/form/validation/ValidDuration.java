package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidDurationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDuration {
    String message() default "{ValidDuration.PromotionForm}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String typeField();

    String daysField();

    String hoursField();

    String minutesField();
}
