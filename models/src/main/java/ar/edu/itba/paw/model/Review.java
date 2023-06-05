package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_reviews")
public class Review {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private long orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    private Order order;

    @Column(nullable = false)
    private int rating;

    @Column(updatable = false)
    private LocalDateTime date;

    @Column
    private String comment;

    public Review() {

    }

    public Review(long orderId, int rating, String comment) {
        this.orderId = orderId;
        this.rating = rating;
        this.date = LocalDateTime.now();
        this.comment = comment;
    }

    public long getOrderId() {
        return orderId;
    }

    public Order getOrder() {
        return order;
    }

    public int getRating() {
        return rating;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
