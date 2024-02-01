package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.BothFieldsNull;

import javax.validation.constraints.Size;

@BothFieldsNull(allowBothNull = false, allowBothNotNull = true, field1Name = "address", field2Name = "name", message = "{BothFieldsNull.UpdateUserAddressForm}")
public class UpdateUserAddressForm {

    @Size(max = 200, message = "{Size.UpdateUserAddressForm.address}")
    private String address;

    @Size(max = 20, message = "{Size.UpdateUserAddressForm.name}")
    private String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
