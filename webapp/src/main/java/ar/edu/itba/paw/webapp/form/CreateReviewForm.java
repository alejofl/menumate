package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateReviewForm {
    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @Size(max = 500)
    private String comment;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentOrNull() {
        return (comment == null || comment.trim().isEmpty()) ? null : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
