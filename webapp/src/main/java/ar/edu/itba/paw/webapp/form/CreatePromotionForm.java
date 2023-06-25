package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.PromotionType;
import ar.edu.itba.paw.webapp.form.validation.EndDateTimeAfterStartDateTime;
import ar.edu.itba.paw.webapp.form.validation.FutureOrPresent;
import ar.edu.itba.paw.webapp.form.validation.NotOverlappingPromotions;
import ar.edu.itba.paw.webapp.form.validation.ValidDuration;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NotOverlappingPromotions
@ValidDuration(typeField = "type", daysField = "days", hoursField = "hours", minutesField = "minutes")
@EndDateTimeAfterStartDateTime(typeField = "type", startDateTimeField = "startDateTime", endDateTimeField = "endDateTime")
public class CreatePromotionForm {
    @NotNull
    private Long sourceProductId;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer percentage;

    @NotNull
    private Integer type;

    @Max(59)
    private Integer minutes;

    @Max(23)
    private Integer hours;

    private Integer days;

    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    public Long getSourceProductId() {
        return sourceProductId;
    }

    public void setSourceProductId(Long sourceProductId) {
        this.sourceProductId = sourceProductId;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getPromotionStartDate() {
        return PromotionType.fromOrdinal(type) == PromotionType.INSTANT ? LocalDateTime.now() : startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public LocalDateTime getPromotionEndDate() {
        return PromotionType.fromOrdinal(type) == PromotionType.INSTANT ?
                LocalDateTime.now().plusDays(days).plusHours(hours).plusMinutes(minutes) :
                endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public float getNormalizedPercentage() {
        return percentage / 100f;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
