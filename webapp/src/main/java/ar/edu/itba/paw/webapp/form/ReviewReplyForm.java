package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReviewReplyForm {
    @NotBlank
    @Size(max = 500)
    private String reply;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
