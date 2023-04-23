package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.webapp.form.CheckoutForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PreProcessingCheckoutFormValidator implements Validator {

    private final Validator validator;

    public PreProcessingCheckoutFormValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return validator.supports(clazz);
    }

    private void validateCheckoutForm(CheckoutForm form, Errors errors) {
        Integer orderType = form.getOrderType();

        // If orderType is null just ignore it; the real validator will take care of it as orderType has @NotNul
        if (orderType == null)
            return;

        if (orderType == OrderType.DINE_IN.ordinal()) {
            form.setAddress(null);
            if (form.getTableNumber() == null) {
                errors.reject("tableNumber", "You must specify a table number");
            }
        } else if (orderType == OrderType.TAKEAWAY.ordinal()) {
            form.setAddress(null);
            form.setTableNumber(null);
        } else if (orderType == OrderType.DELIVERY.ordinal()) {
            form.setTableNumber(null);
            if (form.getAddress() == null) {
                errors.reject("address", "You must specify an address");
            }
        } else {
            errors.reject("Invalid orderType");
        }
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof CheckoutForm) {
            CheckoutForm form = (CheckoutForm) target;
            validateCheckoutForm(form, errors);
        }

        validator.validate(target, errors);
    }
}
