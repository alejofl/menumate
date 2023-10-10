package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CategoryForm {
    @NotBlank
    @Size(max = 50)
    private String name;

    public String getName() {
        return name;
    }

    public String getNameTrimmed() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }
}
