package ar.edu.itba.paw.webapp.form;

import java.util.List;

public class FilterForm extends SearchForm {

    private List<Integer> specialties;

    private List<Integer> tags;

    private Integer orderBy;

    private Boolean descending;

    public List<Integer> getSpecialties() {
        return specialties;
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

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getOrderByOrDefault() {
        return orderBy == null ? 0 : orderBy;
    }

    public Boolean getDescendingOrDefault() {
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
