package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidFieldsByOrderTypeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFieldsByOrderType {
    String message() default "{ValidFieldsByOrderType.CheckoutForm}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String orderTypeField();

    String tableNumberField();

    String addressField();
}
