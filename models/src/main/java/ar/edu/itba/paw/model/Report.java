package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_reports")
public class Report {

    @Id
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "restaurant_id", updatable = false)
    private long restaurantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", insertable = false, updatable = false)
    private Restaurant restaurant;

    @Column(name = "reporter_user_id", updatable = false)
    private Long reporterUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_user_id", insertable = false, updatable = false)
    private User reporter;

    @Column(name = "handler_user_id", updatable = false)
    private Long handlerUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "handler_user_id", insertable = false, updatable = false)
    private User handler;

    @Column(name = "date_reported")
    private LocalDateTime dateReported;

    @Column(name = "date_handled")
    private LocalDateTime dateHandled;

    @Column
    private String comment;


    protected Report() {

    }

    public Report(long restaurantId, Long reporterUserId, String comment) {
        this.restaurantId = restaurantId;
        this.reporterUserId = reporterUserId;
        this.dateReported = LocalDateTime.now();
        this.comment = comment;
    }

    public Long getReportId() {
        return reportId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Long getReporterUserId() {
        return reporterUserId;
    }

    public User getReporter() {
        return reporter;
    }

    public Long getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(Long handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public User getHandler() {
        return handler;
    }

    public LocalDateTime getDateReported() {
        return dateReported;
    }

    public LocalDateTime getDateHandled() {
        return dateHandled;
    }

    public void setDateHandled(LocalDateTime dateHandled) {
        this.dateHandled = dateHandled;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getIsHandled() {
        return this.dateHandled != null;
    }
}
