package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateUserForm {

    @Size(min = 2, max = 48)
    private String name;

    @Pattern(regexp = "es|en")
    private String preferredLanguage;

    public String getName() {
        return name;
    }

    public String getNameTrimmedOrNull() {
        if (name == null)
            return null;
        final String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
