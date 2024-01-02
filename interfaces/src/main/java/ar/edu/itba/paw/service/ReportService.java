package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;

import java.util.Optional;

public interface ReportService {
    Optional<Report> getById(long reportId);

    Report getByIdChecked(long reportId, long restaurantId);

    Report create(long restaurantId, Long reporterUserId, String comment);

    void delete(long reportId, long restaurantId);

    Report markHandled(long reportId, long restaurantId, long handlerUserId);

    PaginatedResult<Pair<Restaurant, Integer>> getCountByRestaurant(int pageNumber, int pageSize);

    PaginatedResult<Report> getByRestaurant(long restaurantId, int pageNumber, int pageSize);
}
