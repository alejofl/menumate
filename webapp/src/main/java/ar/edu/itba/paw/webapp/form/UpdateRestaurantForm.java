package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCodeList;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateRestaurantForm {
    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotNull
    @EnumMessageCode(enumClass = RestaurantSpecialty.class)
    private String specialty;

    @NotBlank
    @Size(max = 300)
    private String description;

    @NotNull
    @Min(1)
    private Integer maxTables;

    @NotEmpty
    @EnumMessageCodeList(enumClass = RestaurantTags.class)
    private List<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialty() {
        return specialty;
    }

    public RestaurantSpecialty getSpecialtyAsEnum() {
        return specialty == null ? null : RestaurantSpecialty.fromCode(specialty);
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxTables() {
        return maxTables;
    }

    public void setMaxTables(Integer maxTables) {
        this.maxTables = maxTables;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<RestaurantTags> getTagsAsEnum() {
        return tags == null ? null : tags.stream().map(RestaurantTags::fromCode).collect(Collectors.toList());
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
