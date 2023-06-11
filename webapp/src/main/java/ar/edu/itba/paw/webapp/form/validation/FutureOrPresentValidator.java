package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class FutureOrPresentValidator implements ConstraintValidator<FutureOrPresent, LocalDateTime> {
    @Override
    public void initialize(FutureOrPresent emailNotInUse) {

    }

    @Override
    public boolean isValid(LocalDateTime datetime, ConstraintValidatorContext constraintValidatorContext) {
        return datetime != null && (datetime.isEqual(LocalDateTime.now()) || datetime.isAfter(LocalDateTime.now()));
    }
}
