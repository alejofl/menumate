package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class SearchForm {
    public final static int DEFAULT_PAGE_NUMBER = 1;
    public final static int DEFAULT_PAGE_SIZE = 12;

    @Size(max=120)
    private String search;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getPage() {
        return page;
    }

    public int getPageOrDefault() {
        return page == null ? DEFAULT_PAGE_NUMBER : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public int getSizeOrDefault() {
        return size == null ? DEFAULT_PAGE_SIZE : size;
    }


    public void setSize(Integer size) {
        this.size = size;
    }
}
