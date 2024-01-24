package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReportRestaurantForm {
    @NotBlank(message = "{NotBlank.ReportRestaurantForm.comment}")
    @Size(max = 500, message = "{Size.ReportRestaurantForm.comment}")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
