package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantOrderBy;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCodeList;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;

public class FilterForm extends SearchForm {

    @QueryParam("specialties")
    @EnumMessageCodeList(enumClass = RestaurantSpecialty.class, message = "{EnumMessageCodeList.FilterForm.specialties}")
    private List<String> specialties;

    @QueryParam("tags")
    @EnumMessageCodeList(enumClass = RestaurantTags.class, message = "{EnumMessageCodeList.FilterForm.tags}")
    private List<String> tags;

    @QueryParam("orderBy")
    @EnumMessageCode(enumClass = RestaurantOrderBy.class, message = "{EnumMessageCodeList.FilterForm.orderBy}")
    private String orderBy;

    @QueryParam("descending")
    private Boolean descending;

    @QueryParam("forEmployeeId")
    private Long forEmployeeId;

    public List<String> getSpecialties() {
        return specialties;
    }

    public List<RestaurantSpecialty> getSpecialtiesAsEnum() {
        return specialties == null || specialties.isEmpty() ? null : specialties.stream().map(RestaurantSpecialty::fromCode).collect(Collectors.toList());
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public Boolean getDescending() {
        return descending;
    }

    public boolean getDescendingOrDefault() {
        return descending != null && descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<RestaurantTags> getTagsAsEnum() {
        return tags == null || tags.isEmpty() ? null : tags.stream().map(RestaurantTags::fromCode).collect(Collectors.toList());
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public RestaurantOrderBy getOrderByAsEnum() {
        return orderBy == null ? null : RestaurantOrderBy.fromCode(orderBy);
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Long getForEmployeeId() {
        return forEmployeeId;
    }

    public void setForEmployeeId(Long forEmployeeId) {
        this.forEmployeeId = forEmployeeId;
    }

    @Override
    public void clear() {
        super.clear();
        specialties = null;
        tags = null;
        orderBy = null;
        descending = null;
        forEmployeeId = null;
    }
}
