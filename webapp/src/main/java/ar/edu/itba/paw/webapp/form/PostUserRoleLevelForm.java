package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PostUserRoleLevelForm extends PatchUserRoleLevelForm {
    @NotNull
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
