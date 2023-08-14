package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.OrderType;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

@Component
public class ValidFieldsByOrderTypeValidator implements ConstraintValidator<ValidFieldsByOrderType, Object> {
    private String orderTypeField;
    private String tableNumberField;
    private String addressField;

    @Override
    public void initialize(ValidFieldsByOrderType validFieldsByOrderType) {
        orderTypeField = validFieldsByOrderType.orderTypeField();
        tableNumberField = validFieldsByOrderType.tableNumberField();
        addressField = validFieldsByOrderType.addressField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            final Field otField = o.getClass().getDeclaredField(orderTypeField);
            otField.setAccessible(true);

            final String orderTypeString = (String) otField.get(o);
            final OrderType orderType = OrderType.fromCode(orderTypeString);
            if (orderType == null) {
                // Return as valid, the validator for OrderType will take care of raising an error.
                return true;
            }

            if (orderType == OrderType.DINE_IN) {
                final Field tnField = o.getClass().getDeclaredField(tableNumberField);
                tnField.setAccessible(true);
                final Integer tableNumber = (Integer) tnField.get(o);
                if (tableNumber == null)
                    return false;
            }

            if (orderType == OrderType.DELIVERY) {
                final Field aField = o.getClass().getDeclaredField(addressField);
                aField.setAccessible(true);
                final String address = (String) aField.get(o);
                if (address == null || address.trim().isEmpty())
                    return false;
            }

            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("ValidFieldsByOrderType exception during validation", e);
        }
    }
}
