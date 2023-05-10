package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.RepeatPasswordsMatch;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@RepeatPasswordsMatch(passwordField = "password", repeatPasswordField = "repeatPassword")
public class ResetPasswordForm {

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
