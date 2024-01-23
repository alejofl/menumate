package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;

public class UpdateRestaurantDeletionForm {

    @NotNull(message = "{NotNull.UpdateRestaurantDeletionForm.delete}")
    @DefaultValue(value = "false")
    private boolean delete;

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean activate) {
        this.delete = activate;
    }
}