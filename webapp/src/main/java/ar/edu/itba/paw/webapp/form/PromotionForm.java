package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.PromotionType;
import ar.edu.itba.paw.webapp.form.validation.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NotOverlappingPromotions
@ValidDuration(typeField = "type", daysField = "days", hoursField = "hours", minutesField = "minutes")
@EndDateTimeAfterStartDateTime(typeField = "type", startDateTimeField = "startDateTime", endDateTimeField = "endDateTime")
public class PromotionForm {
    @NotNull(message = "{NotNull.PromotionForm.sourceProductId}")
    private Long sourceProductId;

    @NotNull(message = "{NotNull.PromotionForm.percentage}")
    @DecimalMin(value = "1", inclusive = true, message = "{DecimalMin.PromotionForm.percentage}")
    @DecimalMax(value = "100", inclusive = true, message = "{DecimalMax.PromotionForm.percentage}")
    @Digits(integer = 3, fraction = 2, message = "{Digits.PromotionForm.percentage}")
    private BigDecimal percentage;

    @NotNull(message = "{NotNull.PromotionForm.type}")
    @EnumMessageCode(enumClass = PromotionType.class, message = "{EnumMessageCode.PromotionForm.type}")
    private String type;

    @Size(min = 0, max = 59, message = "{Size.PromotionForm.minutes}")
    private Integer minutes;

    @Size(min = 0, max = 23, message = "{Size.PromotionForm.hours}")
    private Integer hours;

    @Min(value = 0, message = "{Min.PromotionForm.days}")
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

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getPromotionStartDate() {
        return getTypeAsEnum() == PromotionType.INSTANT ? LocalDateTime.now() : startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public LocalDateTime getPromotionEndDate() {
        if (getTypeAsEnum() == PromotionType.SCHEDULED)
            return endDateTime;

        LocalDateTime endTime = LocalDateTime.now();
        if (days != null)
            endTime = endTime.plusDays(days);
        if (hours != null)
            endTime = endTime.plusHours(hours);
        if (minutes != null)
            endTime = endTime.plusMinutes(minutes);
        return endTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PromotionType getTypeAsEnum() {
        return PromotionType.fromCode(type);
    }
}
