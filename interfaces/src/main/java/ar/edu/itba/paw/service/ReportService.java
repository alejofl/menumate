package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.Optional;

public interface ReportService {
    Optional<Report> getById(long reportId);

    Report create(long restaurantId, Long reporterUserId, String comment);

    Report markHandled(long reportId, long handlerUserId);

    PaginatedResult<Report> getUnhandled(int pageNumber, int pageSize);

    PaginatedResult<Report> getByHandler(long handlerUserId, int pageNumber, int pageSize);

    PaginatedResult<Report> getByRestaurant(long restaurantId, int pageNumber, int pageSize);
}
