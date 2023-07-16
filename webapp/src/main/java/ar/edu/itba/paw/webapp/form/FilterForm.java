package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.RestaurantOrderBy;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;

public class FilterForm extends SearchForm {

    @QueryParam("specialties")
    private List<String> specialties;

    @QueryParam("tags")
    private List<String> tags;

    @QueryParam("orderBy")
    private String orderBy;

    @QueryParam("descending")
    private Boolean descending;

    public List<RestaurantSpecialty> getSpecialties() {
        return specialties == null || specialties.isEmpty() ? null : specialties.stream().map(RestaurantSpecialty::fromCode).collect(Collectors.toList());
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public List<RestaurantTags> getTags() {
        return tags == null || tags.isEmpty() ? null : tags.stream().map(RestaurantTags::fromCode).collect(Collectors.toList());
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public RestaurantOrderBy getOrderBy() {
        return orderBy == null ? null : RestaurantOrderBy.fromCode(orderBy);
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
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
