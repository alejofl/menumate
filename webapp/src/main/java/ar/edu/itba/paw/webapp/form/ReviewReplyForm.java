package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReviewReplyForm {
    @NotBlank(message = "{NotBlank.ReviewReplyForm.reply}")
    @Size(max = 500, message = "{Size.ReviewReplyFrom.reply}")
    private String reply;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
