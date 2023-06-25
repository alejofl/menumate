package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.webapp.form.CreatePromotionForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotOverlappingPromotionsValidator implements ConstraintValidator<NotOverlappingPromotions, Object> {
    @Autowired
    private ProductService productService;

    @Override
    public void initialize(NotOverlappingPromotions annotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (!(o instanceof CreatePromotionForm)) {
            return false;
        }
        CreatePromotionForm form = (CreatePromotionForm) o;
        return !productService.hasPromotionInRange(form.getSourceProductId(), form.getPromotionStartDate(), form.getPromotionEndDate()).isPresent();
    }
}
