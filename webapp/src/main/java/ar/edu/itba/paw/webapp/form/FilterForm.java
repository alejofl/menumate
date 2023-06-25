package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantOrderBy;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;

import java.util.List;
import java.util.stream.Collectors;

public class FilterForm extends SearchForm {

    private List<Integer> specialties;

    private List<Integer> tags;

    private Integer orderBy;

    private Boolean descending;

    public List<Integer> getSpecialties() {
        return specialties;
    }

    public List<RestaurantSpecialty> getSpecialtiesAsEnum() {
        return specialties == null ? null : specialties.stream().map(RestaurantSpecialty::fromOrdinal).collect(Collectors.toList());
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public void setSpecialties(List<Integer> specialties) {
        this.specialties = specialties;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public List<RestaurantTags> getTagsAsEnums() {
        return tags == null ? null : tags.stream().map(RestaurantTags::fromOrdinal).collect(Collectors.toList());
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public RestaurantOrderBy getOrderByAsEnum() {
        return RestaurantOrderBy.fromOrdinal(orderBy == null ? 0 : orderBy);
    }

    public boolean getDescendingOrDefault() {
        return descending != null && descending;
    }

    @Override
    public void clear() {
        super.clear();
        specialties = null;
        tags = null;
        orderBy = null;
        descending = null;
    }
}
