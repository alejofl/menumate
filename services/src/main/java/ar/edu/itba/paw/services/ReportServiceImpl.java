package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.ReportNotFoundException;
import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.ReportDao;
import ar.edu.itba.paw.service.ReportService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportDao reportDao;

    @Override
    public Optional<Report> getById(long reportId) {
        return reportDao.getById(reportId);
    }

    @Override
    public Report getByIdChecked(long reportId, long restaurantId) {
        final Optional<Report> maybeReport = reportDao.getById(reportId);
        if(!maybeReport.isPresent()) {
            LOGGER.error("Report id {} not found", reportId);
            throw new ReportNotFoundException();
        }

        final Report report = maybeReport.get();
        if(report.getRestaurantId() != restaurantId) {
            LOGGER.error("Report id {} is not associated with restaurant id {}", report.getReportId(), restaurantId);
            throw new ReportNotFoundException();
        }

        return report;
    }

    @Transactional
    @Override
    public Report create(long restaurantId, Long reporterUserId, String comment) {
        return reportDao.create(restaurantId, reporterUserId, comment);
    }

    @Transactional
    @Override
    public void delete(long reportId, long restaurantId) {
        final Report report = getByIdChecked(reportId, restaurantId);
        reportDao.delete(report.getReportId());
    }

    @Transactional
    @Override
    public Report markHandled(long reportId, long restaurantId, long handlerUserId) {
        final Report report = getByIdChecked(reportId, restaurantId);

        if (report.getIsHandled()) {
            LOGGER.warn("Attempted to mark report id {} as handled, but report is already handled", report.getReportId());
            throw new IllegalStateException("exception.IllegalStateException.markHandled");
        }

        report.setHandlerUserId(handlerUserId);
        report.setDateHandled(LocalDateTime.now());
        LOGGER.info("Marked report id {} as handled by user id {}", report.getReportId(), report.getHandlerUserId());
        return report;
    }

    @Override
    public PaginatedResult<Pair<Restaurant, Integer>> getCountByRestaurant(int pageNumber, int pageSize) {
        return reportDao.getCountByRestaurant(pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Report> getByRestaurant(long restaurantId, int pageNumber, int pageSize) {
        return reportDao.get(restaurantId, null, null, null, true, pageNumber, pageSize);
    }
}
