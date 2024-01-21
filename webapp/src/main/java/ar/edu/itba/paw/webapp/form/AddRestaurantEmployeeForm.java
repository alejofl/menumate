package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class AddRestaurantEmployeeForm {
    @Email(message = "{Email.addRestaurantEmployeeForm.email}")
    @NotBlank(message = "{NotBlank.addRestaurantEmployeeForm.email}")
    private String email;

    @NotNull(message = "{NotNull.addRestaurantEmployeeForm.role}")
    @EnumMessageCode(enumClass = RestaurantRoleLevel.class, excludeValues = "owner", message = "{EnumMessageCode.addRestaurantEmployeeForm.role}")
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
