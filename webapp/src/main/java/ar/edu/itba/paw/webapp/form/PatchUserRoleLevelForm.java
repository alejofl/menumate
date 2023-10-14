package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import org.hibernate.validator.constraints.NotBlank;

public class PatchUserRoleLevelForm {

    @NotBlank
    @EnumMessageCode(enumClass = UserRoleLevel.class)
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserRoleLevel getRoleAsEnum() {
        return UserRoleLevel.fromCode(role);
    }
}
