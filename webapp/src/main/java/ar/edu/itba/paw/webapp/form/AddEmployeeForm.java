package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.NotOwnerOfRestaurant;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NotOwnerOfRestaurant(emailField = "email", restaurantIdField = "restaurantId")
public class AddEmployeeForm {
    @Email
    @NotBlank
    private String email;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer role;

    @NotNull
    private Integer restaurantId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }
}
