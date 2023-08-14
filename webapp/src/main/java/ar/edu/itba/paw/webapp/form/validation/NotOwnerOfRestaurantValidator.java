package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.RestaurantRoleService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Optional;

@Component
public class NotOwnerOfRestaurantValidator implements ConstraintValidator<NotOwnerOfRestaurant, Object> {
    @Autowired
    private RestaurantRoleService restaurantRoleService;

    @Autowired
    private UserService userService;

    private String emailFieldName;

    private String restaurantIdFieldName;

    @Override
    public void initialize(NotOwnerOfRestaurant notOwnerOfRestaurant) {
        emailFieldName = notOwnerOfRestaurant.emailField();
        restaurantIdFieldName = notOwnerOfRestaurant.restaurantIdField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            final Field emailField = o.getClass().getDeclaredField(emailFieldName);
            emailField.setAccessible(true);
            final Optional<User> user = userService.getByEmail((String) emailField.get(o));
            if (!user.isPresent())
                return true;

            final Field restaurantIdField = o.getClass().getDeclaredField(restaurantIdFieldName);
            restaurantIdField.setAccessible(true);
            return !restaurantRoleService.doesUserHaveRole(user.get().getUserId(), (Long) restaurantIdField.get(o), RestaurantRoleLevel.OWNER);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("NotOwnerOfRestaurantValidator exception during validation", e);
        }
    }
}
