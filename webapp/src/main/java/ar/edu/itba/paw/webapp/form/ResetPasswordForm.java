package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class ResetPasswordForm {

    @NotBlank(message = "{NotBlank.ResetPasswordForm.email}")
    @Email(message = "{Email.ResetPasswordForm.email}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
