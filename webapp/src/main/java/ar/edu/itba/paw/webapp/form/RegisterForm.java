package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.EmailNotInUse;
import ar.edu.itba.paw.webapp.form.validation.RepeatPasswordsMatch;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RepeatPasswordsMatch
public class RegisterForm {

    @NotBlank
    @Email
    @EmailNotInUse
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @NotEmpty
    private String repeatPassword;

    @NotBlank
    @Size(min = 2, max=48)
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
