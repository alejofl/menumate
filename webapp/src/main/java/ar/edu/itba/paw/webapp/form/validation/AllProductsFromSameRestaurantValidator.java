package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.webapp.form.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AllProductsFromSameRestaurantValidator implements ConstraintValidator<AllProductsFromSameRestaurant, Object> {

    private final ProductService productService;

    @Autowired
    public AllProductsFromSameRestaurantValidator(final ProductService productService) {
        this.productService = productService;
    }

    private String restaurantIdField;
    private String cartItemsField;

    @Override
    public void initialize(AllProductsFromSameRestaurant allProductsFromSameRestaurant) {
        restaurantIdField = allProductsFromSameRestaurant.restaurantIdField();
        cartItemsField = allProductsFromSameRestaurant.cartItemsField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            final Field ridField = o.getClass().getDeclaredField(restaurantIdField);
            ridField.setAccessible(true);
            final Long restaurantId = (Long) ridField.get(o);

            if (restaurantId == null) {
                // Return as valid, let the @NotNull check raise the appropriate error.
                return true;
            }

            final Field ciField = o.getClass().getDeclaredField(cartItemsField);
            ciField.setAccessible(true);
            final List<CartItem> cartItems = (List<CartItem>) ciField.get(o);

            if (cartItems == null || cartItems.isEmpty() || cartItems.stream().anyMatch(c -> c.getProductId() == null)) {
                // Return as valid, let the @NotEmpty/@Size check raise the appropriate error.
                return true;
            }

            final List<Long> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
            return productService.areAllProductsFromRestaurant(restaurantId, productIds);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("AllProductsFromSameRestaurant exception during validation", e);
        }
    }
}
