package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidatorConstraint implements ConstraintValidator<Image, Long> {
    private boolean nullable = false;

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(Image image) {
        this.nullable = image.nullable();
    }

    @Override
    public boolean isValid(Long imageId, ConstraintValidatorContext constraintValidatorContext) {
        return nullable || imageService.getById(imageId).isPresent();
    }

}
