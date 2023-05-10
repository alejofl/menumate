package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class DeleteEmployeeForm {
    @NotNull
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
