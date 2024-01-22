package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class NewPasswordForm {

    @Size(min = 8, max = 72, message = "{Size.NewPasswordForm.password}")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
