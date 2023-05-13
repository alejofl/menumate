package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.Image;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateRestaurantForm {
    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotNull
    @Min(0)
    private Integer specialty;

    @NotBlank
    @Size(max = 300)
    private String description;

    @NotNull
    @Min(1)
    private Integer maxTables;

    @Image
    private MultipartFile logo;

    @Image
    private MultipartFile portrait1;

    @Image
    private MultipartFile portrait2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Integer specialty) {
        this.specialty = specialty;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public MultipartFile getLogo() {
        return logo;
    }

    public void setLogo(MultipartFile logo) {
        this.logo = logo;
    }

    public MultipartFile getPortrait1() {
        return portrait1;
    }

    public void setPortrait1(MultipartFile portrait1) {
        this.portrait1 = portrait1;
    }

    public MultipartFile getPortrait2() {
        return portrait2;
    }

    public void setPortrait2(MultipartFile portrait2) {
        this.portrait2 = portrait2;
    }
}
