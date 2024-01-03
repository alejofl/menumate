package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;

public class NewPasswordForm {

    @QueryParam("token")
    @Size(min = 1, max = 32)
    private String token;

    @Size(min = 8, max = 72)
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
