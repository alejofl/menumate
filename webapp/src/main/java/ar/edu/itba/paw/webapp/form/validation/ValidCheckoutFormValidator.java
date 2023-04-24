package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.webapp.form.CheckoutForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCheckoutFormValidator implements ConstraintValidator<ValidCheckoutForm, CheckoutForm> {
    @Override
    public void initialize(ValidCheckoutForm repeatPasswordsMatch) {

    }

    @Override
    public boolean isValid(CheckoutForm registerForm, ConstraintValidatorContext constraintValidatorContext) {
        int orderType = registerForm.getOrderType();

        if (orderType == OrderType.DINE_IN.ordinal()) {
            return true;
        } else if (orderType == OrderType.TAKEAWAY.ordinal()) {
            return false; // NO FUCKING TAKEAWAY
        } else if (orderType == OrderType.DELIVERY.ordinal()) {
            return true;
        }

        return false;
    }
}
