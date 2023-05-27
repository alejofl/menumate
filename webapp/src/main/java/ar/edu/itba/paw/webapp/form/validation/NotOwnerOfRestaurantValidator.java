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
            Field emailField = o.getClass().getDeclaredField(emailFieldName);
            Field restaurantIdField = o.getClass().getDeclaredField(restaurantIdFieldName);
            emailField.setAccessible(true);
            restaurantIdField.setAccessible(true);
            Optional<User> user = userService.getByEmail((String) emailField.get(o));
            if (!user.isPresent())
                return true;
            return !restaurantRoleService.doesUserHaveRole(user.get().getUserId(), (Integer) restaurantIdField.get(o), RestaurantRoleLevel.OWNER);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return true;
        }
    }
}
