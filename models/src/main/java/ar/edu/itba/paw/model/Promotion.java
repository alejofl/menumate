package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotion")
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

    @Column(nullable = false)
    private float discount;

    Promotion() {

    }

    public Promotion(Product source, Product destination, LocalDateTime startDate, LocalDateTime endDate, float discount) {
        this.source = source;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discount = discount;
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

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }
}
