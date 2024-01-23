package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCodeList;
import ar.edu.itba.paw.webapp.form.validation.Image;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantForm {
    @NotBlank(message = "{NotBlank.RestaurantForm.name}")
    @Size(max = 50, message = "{Size.RestaurantForm.name}")
    private String name;

    @NotBlank(message = "{NotBlank.RestaurantForm.address}")
    @Size(max = 200, message = "{Size.RestaurantForm.address}")
    private String address;

    @NotNull(message = "{NotNull.RestaurantForm.specialty}")
    @EnumMessageCode(enumClass = RestaurantSpecialty.class, message = "{EnumMessageCode.RestaurantForm.specialty}")
    private String specialty;

    @NotBlank(message = "{NotBlank.RestaurantForm.description}")
    @Size(max = 300, message = "{Size.RestaurantForm.description}")
    private String description;

    @NotNull(message = "{NotNull.RestaurantForm.maxTables}")
    @Min(value = 1, message = "{Min.RestaurantForm.maxTables}")
    private Integer maxTables;

    @NotEmpty(message = "{NotEmpty.RestaurantForm.tags}")
    @EnumMessageCodeList(enumClass = RestaurantTags.class, message = "{EnumMessageCodeList.RestaurantForm.tags}")
    private List<String> tags;

    @Image(nullable = true)
    private Long logoId;

    @Image(nullable = true)
    private Long portrait1Id;

    @Image(nullable = true)
    private Long portrait2Id;

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

    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public Long getPortrait1Id() {
        return portrait1Id;
    }

    public void setPortrait1Id(Long portrait1Id) {
        this.portrait1Id = portrait1Id;
    }

    public Long getPortrait2Id() {
        return portrait2Id;
    }

    public void setPortrait2Id(Long portrait2Id) {
        this.portrait2Id = portrait2Id;
    }
}
