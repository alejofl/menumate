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

    @Column(insertable = false, updatable = false)
    private LocalDateTime date;

    @Column
    private String comment;

    @Transient
    private OrderItemless orders;

    public Review() {
    }

    public Review(long orderId, int rating, String comment) {
        this.orderId = orderId;
        this.rating = rating;
        this.date = null;
        this.comment = comment;
    }

    // TODO: REMOVE THIS CONSTRUCTOR, It's only here for backwards compatibility until ORM migration is finished.
    public Review(OrderItemless order, int rating, LocalDateTime date, String comment) {
        this.orders = order;
        this.rating = rating;
        this.date = date;
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
