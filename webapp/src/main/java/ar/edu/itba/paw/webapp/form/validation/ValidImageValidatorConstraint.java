package ar.edu.itba.paw.webapp.form.validation;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidImageValidatorConstraint implements ConstraintValidator<ValidImage, FormDataBodyPart> {

    @Override
    public void initialize(ValidImage validImage) {

    }
    @Override
    public boolean isValid(FormDataBodyPart formDataBodyPart, ConstraintValidatorContext constraintValidatorContext) {
        return formDataBodyPart != null && formDataBodyPart.getMediaType().toString().startsWith("image/");
    }
}
