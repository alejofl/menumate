package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PutReviewForm {

    @NotNull(message = "{NotNull.PutReviewForm.rating}")
    @Min(value = 0, message = "{Min.PutReviewForm.rating}")
    @Max(value = 5, message = "{Max.PutReviewForm.rating}")
    private int rating;

    @Size(max = 500, message = "{Size.PutReviewForm.comment}")
    private String comment;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentTrimmedOrNull() {
        if (comment == null)
            return null;
        final String trimmed = comment.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
