package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotOwnerOfRestaurantValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotOwnerOfRestaurant {
    String message() default "Cannot add roles to owner of restaurant";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String emailField();

    String restaurantIdField();
}
