package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;

public class UpdateRestaurantIsActiveForm {

    @NotNull(message = "{NotNull.UpdateRestaurantIsActiveForm.activate}")
    @DefaultValue(value = "false")
    private boolean activate;

    public boolean getActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }
}