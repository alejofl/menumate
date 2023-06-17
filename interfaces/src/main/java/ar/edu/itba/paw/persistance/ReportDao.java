package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.Optional;

public interface ReportDao {
    Optional<Report> getById(long reportId);

    Report create(long restaurantId, Long reporterUserId, String comment);

    /**
     * Gets reports optionally sorting by restaurant, reporter id, handler id, whether the report was handled, and
     * sorted by date either descending or ascending. Any nullable parameter can be set to null to not apply said
     * filter. If isHandled is true, the results are sorted by date handled. Otherwise, the results are sorted by
     * date reported.
     */
    PaginatedResult<Report> get(Long restaurantId, Long reporterUserId, Long handlerUserId, Boolean isHandled, boolean descending, int pageNumber, int pageSize);
}
