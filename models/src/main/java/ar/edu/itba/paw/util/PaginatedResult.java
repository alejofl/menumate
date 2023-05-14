package ar.edu.itba.paw.util;

import java.util.List;

public class PaginatedResult<T> {
    private final List<T> result;
    private final int pageNumber, pageSize, totalCount;

    public PaginatedResult(List<T> result, int pageNumber, int pageSize, int totalCount) {
        this.result = result;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public List<T> getResult() {
        return result;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPageCount() {
        return (totalCount + pageSize - 1) / pageSize;
    }
}
