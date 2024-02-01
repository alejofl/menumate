package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

public class PostUserRoleLevelForm extends PatchUserRoleLevelForm {
    @NotNull(message = "{NotNull.PostUserRoleLevelForm.email}")
    @Email(message = "{Email.PostUserRoleLevelForm.email}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
