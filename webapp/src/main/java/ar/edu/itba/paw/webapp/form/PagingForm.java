package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Min;

public class PagingForm {

    @Min(1)
    private Integer size;

    @Min(1)
    private Integer page;

    public Integer getPage() {
        return page;
    }

    public int getPageOrDefault() {
        return page == null ? 1 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public int getSizeOrDefault(int defaultPageSize) {
        return size == null ? defaultPageSize : size;
    }


    public void setSize(Integer size) {
        this.size = size;
    }

    public void clear() {
        page = null;
        size = null;
    }
}
