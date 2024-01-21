package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class AddUserAddressForm {

    @NotBlank(message = "{NotBlank.addUserAddressForm.address}")
    @Size(max = 200, message = "{Size.addUserAddressForm.address}")
    private String address;

    @Size(max = 20, message = "{Size.addUserAddressForm.name}")
    private String name;

    public String getAddress() {
        return address;
    }

    public String getAddressTrimmedOrNull() {
        if (address == null)
            return null;
        final String trimmed = address.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
