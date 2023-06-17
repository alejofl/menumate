package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.persistance.ReportDao;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ReportJpaDao implements ReportDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Report> getById(long reportId) {
        return Optional.ofNullable(em.find(Report.class, reportId));
    }

    @Override
    public Report create(long restaurantId, Long reporterUserId, String comment) {
        final Report report = new Report(restaurantId, reporterUserId, comment);
        em.persist(report);
        LOGGER.info("Created report with id {} for restaurant {}", report.getReportId(), report.getRestaurantId());
        return report;
    }

    private void appendFilterConditions(StringBuilder sqlBuilder, Long restaurantId, Long reporterUserId, Long handlerUserId, Boolean isHandled) {
        boolean isFirst = true;

        if (restaurantId != null) {
            sqlBuilder.append(" WHERE");
            isFirst = false;
            sqlBuilder.append(" restaurant_id = :restaurantId");
        }

        if (reporterUserId != null) {
            sqlBuilder.append(isFirst ? " WHERE" : " AND");
            isFirst = false;
            sqlBuilder.append(" reporter_user_id = :reporterId");
        }

        if (handlerUserId != null) {
            sqlBuilder.append(isFirst ? " WHERE" : " AND");
            isFirst = false;
            sqlBuilder.append(" handler_user_id = :handlerId");
        }

        if (isHandled != null) {
            sqlBuilder.append(isFirst ? " WHERE" : " AND");
            isFirst = false;
            sqlBuilder.append(" date_handled IS ").append(isHandled ? "NOT NULL" : "NULL");
        }
    }

    @Override
    public PaginatedResult<Report> get(Long restaurantId, Long reporterUserId, Long handlerUserId, Boolean isHandled, boolean descending, int pageNumber, int pageSize) {
        Utils.validatePaginationParams(pageNumber, pageSize);

        StringBuilder sqlBuilder = new StringBuilder("SELECT report_id FROM restaurant_reports");
        appendFilterConditions(sqlBuilder, restaurantId, reporterUserId, handlerUserId, isHandled);

        sqlBuilder.append(" ORDER BY ")
                .append(isHandled != null && isHandled ? "date_handled" : "date_reported")
                .append(descending ? " DESC" : " ASC")
                .append(", report_id");

        Query nativeQuery = em.createNativeQuery(sqlBuilder.toString());
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);
        if (restaurantId != null) nativeQuery.setParameter("restaurantId", restaurantId);
        if (reporterUserId != null) nativeQuery.setParameter("reporterId", reporterUserId);
        if (handlerUserId != null) nativeQuery.setParameter("handlerId", handlerUserId);

        final List<Long> idList = nativeQuery.getResultList().stream().mapToLong(n -> ((Number) n).longValue()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        sqlBuilder.setLength(0);
        sqlBuilder.append("SELECT COUNT(*) FROM restaurant_reports");
        appendFilterConditions(sqlBuilder, restaurantId, reporterUserId, handlerUserId, isHandled);

        Query countQuery = em.createNativeQuery(sqlBuilder.toString());
        if (restaurantId != null) countQuery.setParameter("restaurantId", restaurantId);
        if (reporterUserId != null) countQuery.setParameter("reporterId", reporterUserId);
        if (handlerUserId != null) countQuery.setParameter("handlerId", handlerUserId);
        int count = ((Number) countQuery.getSingleResult()).intValue();

        if (idList.isEmpty())
            return new PaginatedResult<>(Collections.emptyList(), pageNumber, pageSize, count);

        sqlBuilder.setLength(0);
        sqlBuilder.append("FROM Report WHERE reportId IN :idList ORDER BY ")
                .append(isHandled != null && isHandled ? "dateHandled" : "dateReported")
                .append(descending ? " DESC" : " ASC")
                .append(", reportId");

        TypedQuery<Report> resultsQuery = em.createQuery(
                sqlBuilder.toString(),
                Report.class
        );
        resultsQuery.setParameter("idList", idList);
        final List<Report> results = resultsQuery.getResultList();

        return new PaginatedResult<>(results, pageNumber, pageSize, count);
    }
}
