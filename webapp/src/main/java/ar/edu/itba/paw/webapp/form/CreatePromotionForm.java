package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.EndDateTimeAfterStartDateTime;
import ar.edu.itba.paw.webapp.form.validation.FutureOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EndDateTimeAfterStartDateTime(startDateTimeField = "startDateTime", endDateTimeField = "endDateTime")
public class CreatePromotionForm {
    @NotNull
    private Integer sourceProductId;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer percentage;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    public Integer getSourceProductId() {
        return sourceProductId;
    }

    public void setSourceProductId(Integer sourceProductId) {
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

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public float getNormalizedPercentage() {
        return percentage / 100f;
    }
}
