package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.EmailNotInUse;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "{NotBlank.RegisterForm.email}")
    @Email(message = "{Email.RegisterForm.email}")
    @EmailNotInUse
    private String email;

    @NotBlank(message = "{NotBlank.RegisterForm.password}")
    @Size(min = 8, max = 72, message = "{Size.RegisterForm.password}")
    private String password;

    @NotBlank(message = "{NotBlank.RegisterForm.name}")
    @Size(min = 2, max = 48, message = "{Size.RegisterForm.name}")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
