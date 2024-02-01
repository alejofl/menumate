package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.model.PromotionType;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.webapp.form.PromotionForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class NotOverlappingPromotionsValidator implements ConstraintValidator<NotOverlappingPromotions, Object> {
    @Autowired
    private ProductService productService;

    @Override
    public void initialize(NotOverlappingPromotions annotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (!(o instanceof PromotionForm))
            return false;
        PromotionForm form = (PromotionForm) o;

        Long productId = form.getSourceProductId();
        if (productId == null)
            return true; // Let the @NotNull handle it

        LocalDateTime startDate = form.getPromotionStartDate();
        LocalDateTime endDate = form.getPromotionEndDate();
        return startDate != null && endDate != null && startDate.isBefore(endDate)
                && !productService.hasPromotionInRange(productId, startDate, endDate).isPresent();
    }
}
