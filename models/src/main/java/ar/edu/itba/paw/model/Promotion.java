package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promotions_promotion_id_seq")
    @SequenceGenerator(sequenceName = "promotions_promotion_id_seq", name = "promotions_promotion_id_seq", allocationSize = 1)
    @Column(name = "promotion_id", nullable = false, updatable = false)
    private Long promotionId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "source_id", referencedColumnName = "product_id", updatable = false)
    private Product source;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_id", referencedColumnName = "product_id", updatable = false)
    private Product destination;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    Promotion() {

    }

    public Promotion(Product source, Product destination, LocalDateTime startDate, LocalDateTime endDate) {
        this.source = source;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public Product getSource() {
        return source;
    }

    public Product getDestination() {
        return destination;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDiscountPercentage() {
        BigDecimal hundred = BigDecimal.valueOf(100);
        return hundred.subtract(destination.getPrice().multiply(hundred).divide(source.getPrice(), 2, RoundingMode.CEILING));
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !startDate.isAfter(now) && (endDate == null || endDate.isAfter(now));
    }

    public boolean hasStarted() {
        return !startDate.isAfter(LocalDateTime.now());
    }

    public boolean hasEnded() {
        return endDate != null && !endDate.isAfter(LocalDateTime.now());
    }

    @Override
    public int hashCode() {
        return Objects.hash(promotionId, source.getProductId(), destination.getProductId(), startDate, endDate, getDiscountPercentage());
    }
}
