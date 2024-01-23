package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDto {

    private long reportId;
    private long restaurantId;
    private Long reporterId;
    private Long handlerId;
    private LocalDateTime dateReported;
    private LocalDateTime dateHandled;
    private boolean handled;
    private String comment;

    private URI selfUrl;
    private URI restaurantUrl;
    private URI reporterUrl;
    private URI handlerURL;

    public static ReportDto fromReport(final UriInfo uriInfo, Report report) {
        final ReportDto dto = new ReportDto();
        dto.reportId = report.getReportId();
        dto.restaurantId = report.getRestaurantId();
        dto.reporterId = report.getReporterUserId();
        dto.dateReported = report.getDateReported();
        dto.comment = report.getComment();
        dto.handled = report.getIsHandled();

        if(report.getIsHandled()) {
            dto.handlerId = report.getHandlerUserId();
            dto.dateHandled = report.getDateHandled();
            dto.handlerURL = UriUtils.getUserUri(uriInfo, report.getHandlerUserId());
        }

        dto.selfUrl = UriUtils.getReportUri(uriInfo, report.getReportId(), report.getRestaurantId());
        dto.restaurantUrl = UriUtils.getRestaurantUri(uriInfo, report.getRestaurantId());
        dto.reporterUrl = dto.reporterId != null ? UriUtils.getUserUri(uriInfo, report.getReporterUserId()) : null;
        
        return dto;
    }

    public static List<ReportDto> fromReportCollection(final UriInfo uriInfo, Collection<Report> reports) {
        return reports.stream().map(r -> fromReport(uriInfo, r)).collect(Collectors.toList());
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public LocalDateTime getDateReported() {
        return dateReported;
    }

    public void setDateReported(LocalDateTime dateReported) {
        this.dateReported = dateReported;
    }

    public LocalDateTime getDateHandled() {
        return dateHandled;
    }

    public void setDateHandled(LocalDateTime dateHandled) {
        this.dateHandled = dateHandled;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getRestaurantUrl() {
        return restaurantUrl;
    }

    public void setRestaurantUrl(URI restaurantUrl) {
        this.restaurantUrl = restaurantUrl;
    }

    public URI getReporterUrl() {
        return reporterUrl;
    }

    public void setReporterUrl(URI reporterUrl) {
        this.reporterUrl = reporterUrl;
    }

    public URI getHandlerURL() {
        return handlerURL;
    }

    public void setHandlerURL(URI handlerURL) {
        this.handlerURL = handlerURL;
    }
}
