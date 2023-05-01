package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidatorConstraint implements ConstraintValidator<Image, MultipartFile> {
    private static final long MAX_SIZE = 1024 * 1024 * 5; // 1024 B = 1 KB && 1024 B * 1024 = 1 MB ==> MAX_SIZE = 5 MB
    private static final String ACCEPTED_MIME_TYPES = "image/";

    public static long getMaxSize() {
        return MAX_SIZE;
    }

    @Override
    public void initialize(Image image) {

    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return multipartFile != null && !multipartFile.isEmpty() && multipartFile.getSize() < MAX_SIZE && multipartFile.getContentType().contains(ACCEPTED_MIME_TYPES);
    }
}
