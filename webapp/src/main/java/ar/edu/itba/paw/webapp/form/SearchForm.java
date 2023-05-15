package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class SearchForm extends PagingForm {

    @Size(max = 120)
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public void clear() {
        super.clear();
        search = null;
    }
}
