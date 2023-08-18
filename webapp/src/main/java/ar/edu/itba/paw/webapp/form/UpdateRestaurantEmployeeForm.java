package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;

import javax.validation.constraints.NotNull;

public class UpdateRestaurantEmployeeForm {
    @NotNull
    @EnumMessageCode(enumClass = RestaurantRoleLevel.class, excludeValues = "owner")
    private String role;

    public String getRole() {
        return role;
    }

    public RestaurantRoleLevel getRoleAsEnum() {
        return RestaurantRoleLevel.fromCode(role);
    }

    public void setRole(String role) {
        this.role = role;
    }
}
