package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FutureOrPresentValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresent {
    String message() default "{FutureOrPresent.PromotionForm.startDateTime}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
