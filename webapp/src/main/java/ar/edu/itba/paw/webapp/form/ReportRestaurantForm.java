package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReportRestaurantForm {
    private long reporterId;

    @NotBlank
    @Size(max = 500)
    private String comment;

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long userId) {
        this.reporterId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
